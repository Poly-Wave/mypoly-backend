from datetime import date, datetime
from typing import Any, Dict

from app.clients.bill_info_api_client import BillInfoApiClient
from app.clients.gemini_client import GeminiApiError, GeminiClient
from app.config import Settings
from app.db import get_connection
from app.repositories.ai_repository import AiRepository
from app.repositories.batch_repository import BatchRepository
from app.repositories.bill_repository import BillRepository
from app.repositories.category_repository import CategoryRepository
from app.utils.hash_utils import hash_bill_ai_input


class BatchRunner:
    def __init__(self, settings: Settings):
        self.settings = settings

    def _resolve_dates(self, from_date: date | None, to_date: date | None) -> tuple[date, date]:
        min_proposal_date = datetime.strptime(self.settings.min_proposal_date, "%Y-%m-%d").date()
        today = date.today()

        resolved_from = from_date or min_proposal_date
        resolved_to = to_date or today

        if resolved_from < min_proposal_date:
            resolved_from = min_proposal_date

        if resolved_to < resolved_from:
            raise ValueError(f"Invalid date range: from_date={resolved_from}, to_date={resolved_to}")

        return resolved_from, resolved_to

    def _is_collect_target_bill(
        self,
        proposal_date: date | None,
        min_proposal_date: date,
    ) -> tuple[bool, str]:
        if proposal_date is None:
            return False, "proposal_date_missing"

        if proposal_date < min_proposal_date:
            return False, f"proposal_date_lt_min({proposal_date}<{min_proposal_date})"

        return True, "collect_target"

    def _should_mark_permanent_failed(self, retry_count_after_update: int, error: GeminiApiError) -> bool:
        if error.code in {"API_KEY_INVALID", "INVALID_REQUEST", "INVALID_CATEGORY_MAPPING"}:
            return True

        return retry_count_after_update >= self.settings.bill_batch_ai_max_retry_count

    def _build_ai_input(self, bill: Dict[str, Any]) -> Dict[str, Any]:
        return {
            "official_title": bill.get("official_title") or "",
            "summary_raw": bill.get("summary_raw") or "",
        }

    def _build_ai_input_hash(self, bill: Dict[str, Any]) -> str:
        ai_input = self._build_ai_input(bill)
        return hash_bill_ai_input(
            official_title=ai_input["official_title"],
            summary_raw=ai_input["summary_raw"],
        )

    def _collect_bills(
        self,
        batch_repo: BatchRepository,
        bill_repo: BillRepository,
        api_client: BillInfoApiClient,
        batch_run_id: int,
        start_date: date,
        end_date: date,
        min_proposal_date: date,
    ) -> Dict[str, int]:
        api_seen = 0
        eligible_found = 0
        inserted = 0
        updated = 0
        skipped = 0
        failed = 0

        for bill in api_client.iter_bills(start_date, end_date):
            api_seen += 1
            external_bill_id = bill["external_bill_id"]
            proposal_date = bill.get("proposal_date")

            allowed, reason = self._is_collect_target_bill(
                proposal_date=proposal_date,
                min_proposal_date=min_proposal_date,
            )

            if not allowed:
                skipped += 1
                print(
                    f"[BATCH][COLLECT] skip api_seen={api_seen} external_bill_id={external_bill_id} "
                    f"proposal_date={proposal_date} reason={reason}",
                    flush=True,
                )
                continue

            eligible_found += 1

            try:
                bill_id, action = bill_repo.upsert_bill(bill)
                bill_repo.replace_proposers(bill_id, bill)
                bill_repo.insert_status_history_if_needed(bill_id, bill)

                batch_repo.add_item(
                    batch_run_id=batch_run_id,
                    item_type="BILL_COLLECT",
                    target_external_id=external_bill_id,
                    target_internal_id=bill_id,
                    item_status="SUCCESS",
                    action_type=action,
                    payload={
                        "proposal_date": str(bill.get("proposal_date")),
                        "official_title": bill.get("official_title"),
                    },
                )

                if action == "INSERT":
                    inserted += 1
                elif action == "UPDATE":
                    updated += 1
                else:
                    skipped += 1

                self._commit(batch_repo.conn)
                print(
                    f"[BATCH][COLLECT] success external_bill_id={external_bill_id} action={action}",
                    flush=True,
                )

            except Exception as exc:
                failed += 1
                self._rollback(batch_repo.conn)
                batch_repo.add_item(
                    batch_run_id=batch_run_id,
                    item_type="BILL_COLLECT",
                    target_external_id=external_bill_id,
                    target_internal_id=None,
                    item_status="FAILED",
                    action_type=None,
                    error_message=str(exc),
                    payload={
                        "proposal_date": str(proposal_date) if proposal_date else None,
                        "official_title": bill.get("official_title"),
                    },
                )
                self._commit(batch_repo.conn)
                print(f"[BATCH][COLLECT][ERROR] external_bill_id={external_bill_id} error={exc}", flush=True)

        return {
            "api_seen": api_seen,
            "eligible_found": eligible_found,
            "inserted": inserted,
            "updated": updated,
            "skipped": skipped,
            "failed": failed,
        }

    def _process_ai_queue(
        self,
        batch_repo: BatchRepository,
        bill_repo: BillRepository,
        ai_repo: AiRepository,
        category_repo: CategoryRepository,
        gemini_client: GeminiClient,
        batch_run_id: int,
        min_proposal_date: date,
    ) -> Dict[str, Any]:
        stale_reset = bill_repo.reset_stale_processing(
            timeout_minutes=self.settings.bill_batch_ai_processing_timeout_minutes
        )
        if stale_reset:
            self._commit(batch_repo.conn)
            print(f"[BATCH][AI] reset stale processing count={stale_reset}", flush=True)

        candidate_bills = bill_repo.get_pending_ai_bills(
            limit=self.settings.bill_batch_max_ai_per_run,
            min_proposal_date=min_proposal_date,
        )
        category_map = category_repo.get_active_category_map()

        print(
            f"[BATCH][AI] candidate_count={len(candidate_bills)} daily_limit={self.settings.bill_batch_max_ai_per_run}",
            flush=True,
        )

        attempted = 0
        success = 0
        skipped = 0
        failed = 0
        quota_exhausted = False

        for bill in candidate_bills:
            bill_id = bill["id"]
            external_bill_id = bill["external_bill_id"]
            attempted += 1

            try:
                bill_repo.mark_ai_processing(bill_id)
                self._commit(batch_repo.conn)

                current_analysis = ai_repo.get_current_analysis(bill_id)
                ai_input = self._build_ai_input(bill)
                ai_input_hash = self._build_ai_input_hash(bill)

                if current_analysis and current_analysis.get("source_text_hash") == ai_input_hash:
                    bill_repo.mark_ai_skipped(
                        bill_id=bill_id,
                        analysis_id=current_analysis.get("id"),
                    )
                    batch_repo.add_item(
                        batch_run_id=batch_run_id,
                        item_type="BILL_AI_ANALYZE",
                        target_external_id=external_bill_id,
                        target_internal_id=bill_id,
                        item_status="SKIPPED",
                        action_type="NO_CHANGE",
                        payload={
                            "reason": "same_ai_input_hash_existing_current_analysis",
                            "analysis_id": current_analysis.get("id"),
                        },
                    )
                    self._commit(batch_repo.conn)
                    skipped += 1
                    print(
                        f"[BATCH][AI] skipped external_bill_id={external_bill_id} reason=same_ai_input_hash_existing_current_analysis",
                        flush=True,
                    )
                    continue

                ai_output = gemini_client.analyze_bill(
                    title=ai_input["official_title"],
                    body=ai_input["summary_raw"],
                )

                seen_category_ids = set()
                category_ids = []
                for code in ai_output["categories"]:
                    category_id = category_map.get(code)
                    if category_id and category_id not in seen_category_ids:
                        seen_category_ids.add(category_id)
                        category_ids.append(category_id)

                if not category_ids:
                    raise GeminiApiError(
                        code="INVALID_CATEGORY_MAPPING",
                        message=f"No valid category mapped from AI output: {ai_output['categories']}",
                        retryable=False,
                    )

                analysis_id = ai_repo.insert_success_analysis(
                    bill_id=bill_id,
                    source_text_hash=ai_input_hash,
                    model_name=self.settings.gemini_model,
                    prompt_version=self.settings.prompt_version,
                    temperature=self.settings.gemini_temperature,
                    analysis_input=ai_input,
                    analysis_output=ai_output,
                    category_ids=category_ids,
                    axis_for=ai_output["vote"].get("for", {}),
                    axis_against=ai_output["vote"].get("against", {}),
                )

                bill_repo.mark_ai_success(bill_id=bill_id, analysis_id=analysis_id)

                batch_repo.add_item(
                    batch_run_id=batch_run_id,
                    item_type="BILL_AI_ANALYZE",
                    target_external_id=external_bill_id,
                    target_internal_id=bill_id,
                    item_status="SUCCESS",
                    action_type="INSERT",
                    payload={
                        "analysis_id": analysis_id,
                        "headline": ai_output["headline"],
                        "categories": ai_output["categories"],
                    },
                )

                self._commit(batch_repo.conn)
                success += 1
                print(f"[BATCH][AI] success external_bill_id={external_bill_id} analysis_id={analysis_id}", flush=True)

            except GeminiApiError as exc:
                self._rollback(batch_repo.conn)

                ai_input = self._build_ai_input(bill)
                ai_input_hash = self._build_ai_input_hash(bill)
                retry_count_after_update = int(bill.get("ai_retry_count", 0)) + 1

                analysis_id = ai_repo.insert_failed_analysis(
                    bill_id=bill_id,
                    source_text_hash=ai_input_hash,
                    model_name=self.settings.gemini_model,
                    prompt_version=self.settings.prompt_version,
                    temperature=self.settings.gemini_temperature,
                    analysis_input=ai_input,
                    error_message=str(exc),
                )

                if exc.quota_exhausted:
                    bill_repo.mark_ai_retry_wait(
                        bill_id=bill_id,
                        error_code=exc.code,
                        error_message=str(exc),
                        wait_minutes=self.settings.bill_batch_ai_quota_retry_wait_minutes,
                    )
                    batch_repo.add_item(
                        batch_run_id=batch_run_id,
                        item_type="BILL_AI_ANALYZE",
                        target_external_id=external_bill_id,
                        target_internal_id=bill_id,
                        item_status="FAILED",
                        action_type=None,
                        error_message=str(exc),
                        payload={
                            "reason": "quota_exhausted",
                            "analysis_id": analysis_id,
                        },
                    )
                    self._commit(batch_repo.conn)
                    failed += 1
                    quota_exhausted = True
                    print(f"[BATCH][AI] quota exhausted, stop ai queue external_bill_id={external_bill_id}", flush=True)
                    break

                if self._should_mark_permanent_failed(retry_count_after_update, exc):
                    bill_repo.mark_ai_permanent_failed(
                        bill_id=bill_id,
                        error_code=exc.code,
                        error_message=str(exc),
                    )
                else:
                    bill_repo.mark_ai_retry_wait(
                        bill_id=bill_id,
                        error_code=exc.code,
                        error_message=str(exc),
                        wait_minutes=self.settings.bill_batch_ai_retry_wait_minutes,
                    )

                batch_repo.add_item(
                    batch_run_id=batch_run_id,
                    item_type="BILL_AI_ANALYZE",
                    target_external_id=external_bill_id,
                    target_internal_id=bill_id,
                    item_status="FAILED",
                    action_type=None,
                    error_message=str(exc),
                    payload={
                        "analysis_id": analysis_id,
                        "error_code": exc.code,
                        "retryable": exc.retryable,
                        "quota_exhausted": exc.quota_exhausted,
                    },
                )
                self._commit(batch_repo.conn)
                failed += 1
                print(f"[BATCH][AI][ERROR] external_bill_id={external_bill_id} error={exc}", flush=True)

            except Exception as exc:
                self._rollback(batch_repo.conn)

                ai_input = self._build_ai_input(bill)
                ai_input_hash = self._build_ai_input_hash(bill)

                analysis_id = ai_repo.insert_failed_analysis(
                    bill_id=bill_id,
                    source_text_hash=ai_input_hash,
                    model_name=self.settings.gemini_model,
                    prompt_version=self.settings.prompt_version,
                    temperature=self.settings.gemini_temperature,
                    analysis_input=ai_input,
                    error_message=str(exc),
                )

                bill_repo.mark_ai_retry_wait(
                    bill_id=bill_id,
                    error_code="UNEXPECTED_ERROR",
                    error_message=str(exc),
                    wait_minutes=self.settings.bill_batch_ai_retry_wait_minutes,
                )

                batch_repo.add_item(
                    batch_run_id=batch_run_id,
                    item_type="BILL_AI_ANALYZE",
                    target_external_id=external_bill_id,
                    target_internal_id=bill_id,
                    item_status="FAILED",
                    action_type=None,
                    error_message=str(exc),
                    payload={
                        "analysis_id": analysis_id,
                        "error_code": "UNEXPECTED_ERROR",
                    },
                )
                self._commit(batch_repo.conn)
                failed += 1
                print(f"[BATCH][AI][ERROR] external_bill_id={external_bill_id} error={exc}", flush=True)

        return {
            "attempted": attempted,
            "success": success,
            "skipped": skipped,
            "failed": failed,
            "quota_exhausted": quota_exhausted,
        }

    def _commit(self, conn):
        conn.commit()

    def _rollback(self, conn):
        conn.rollback()

    def run(self, from_date: date | None = None, to_date: date | None = None, trigger_type: str | None = None):
        trigger_type = trigger_type or self.settings.bill_batch_trigger_type
        start_date, end_date = self._resolve_dates(from_date, to_date)
        min_proposal_date = datetime.strptime(self.settings.min_proposal_date, "%Y-%m-%d").date()

        print(
            f"[BATCH] start trigger_type={trigger_type} start_date={start_date} end_date={end_date} min_proposal_date={min_proposal_date}",
            flush=True,
        )

        conn = get_connection(self.settings)
        conn.autocommit = False

        batch_run_id = None

        try:
            batch_repo = BatchRepository(conn, self.settings.db_schema)
            bill_repo = BillRepository(conn, self.settings.db_schema)
            ai_repo = AiRepository(conn, self.settings.db_schema)
            category_repo = CategoryRepository(conn, self.settings.db_schema)

            api_client = BillInfoApiClient(self.settings)
            gemini_client = GeminiClient(self.settings)

            batch_run_id = batch_repo.create_run(
                job_type=self.settings.bill_batch_job_type,
                trigger_type=trigger_type,
                image_tag=self.settings.image_tag,
                git_commit_sha=self.settings.git_commit_sha,
            )
            self._commit(conn)
            print(f"[BATCH] batch_run_id={batch_run_id} created", flush=True)

            collect_result = self._collect_bills(
                batch_repo=batch_repo,
                bill_repo=bill_repo,
                api_client=api_client,
                batch_run_id=batch_run_id,
                start_date=start_date,
                end_date=end_date,
                min_proposal_date=min_proposal_date,
            )

            ai_result = self._process_ai_queue(
                batch_repo=batch_repo,
                bill_repo=bill_repo,
                ai_repo=ai_repo,
                category_repo=category_repo,
                gemini_client=gemini_client,
                batch_run_id=batch_run_id,
                min_proposal_date=min_proposal_date,
            )

            total_inserted = collect_result["inserted"] + ai_result["success"]
            total_updated = collect_result["updated"]
            total_skipped = collect_result["skipped"] + ai_result["skipped"]
            total_failed = collect_result["failed"] + ai_result["failed"]

            if total_failed == 0 and not ai_result["quota_exhausted"]:
                run_status = "SUCCESS"
                message = (
                    f"collect_api_seen={collect_result['api_seen']}, "
                    f"collect_eligible={collect_result['eligible_found']}, "
                    f"ai_attempted={ai_result['attempted']}, "
                    f"ai_success={ai_result['success']}"
                )
            else:
                run_status = "PARTIAL_SUCCESS"
                message = (
                    f"collect_api_seen={collect_result['api_seen']}, "
                    f"collect_eligible={collect_result['eligible_found']}, "
                    f"ai_attempted={ai_result['attempted']}, "
                    f"ai_success={ai_result['success']}, "
                    f"ai_failed={ai_result['failed']}, "
                    f"quota_exhausted={ai_result['quota_exhausted']}"
                )

            batch_repo.finish_run(
                batch_run_id=batch_run_id,
                run_status=run_status,
                records_found=collect_result["eligible_found"],
                records_inserted=total_inserted,
                records_updated=total_updated,
                records_skipped=total_skipped,
                records_failed=total_failed,
                message=message,
            )
            self._commit(conn)

            result = {
                "batch_run_id": batch_run_id,
                "run_status": run_status,
                "collect": collect_result,
                "ai": ai_result,
                "message": message,
            }
            print(f"[BATCH] done result={result}", flush=True)
            return result

        except Exception as exc:
            self._rollback(conn)

            if batch_run_id is not None:
                try:
                    batch_repo = BatchRepository(conn, self.settings.db_schema)
                    batch_repo.finish_run(
                        batch_run_id=batch_run_id,
                        run_status="FAILED",
                        records_found=0,
                        records_inserted=0,
                        records_updated=0,
                        records_skipped=0,
                        records_failed=1,
                        message=str(exc),
                    )
                    self._commit(conn)
                except Exception:
                    self._rollback(conn)

            print(f"[BATCH][FATAL] {exc}", flush=True)
            raise
        finally:
            conn.close()