from datetime import date, datetime
from typing import Any, Dict

from app.clients.assembly_open_api_client import AssemblyOpenApiClient
from app.clients.bill_info_api_client import BillInfoApiClient
from app.clients.gemini_client import GeminiApiError, GeminiClient
from app.config import Settings
from app.db import get_connection
from app.repositories.ai_repository import AiRepository
from app.repositories.batch_repository import BatchRepository
from app.repositories.bill_repository import BillRepository
from app.repositories.category_repository import CategoryRepository
from app.repositories.member_repository import MemberRepository
from app.repositories.vote_repository import VoteRepository
from app.utils.hash_utils import hash_bill_ai_input


class BatchRunner:
    def __init__(self, settings: Settings):
        self.settings = settings

    def _is_collect_target_bill(
        self,
        proposal_date: date | None,
        min_proposal_date: date,
    ) -> tuple[bool, str]:
        if proposal_date is None:
            return False, "제안일이 없어 수집 대상에서 제외됨"

        if proposal_date < min_proposal_date:
            return False, f"최소 수집일보다 이전 의안이라 제외됨 ({proposal_date} < {min_proposal_date})"

        return True, "수집 대상"

    def _should_mark_permanent_failed(self, retry_count_after_update: int, error: GeminiApiError) -> bool:
        if error.code in {"INVALID_CATEGORY_MAPPING"}:
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
        min_proposal_date: date,
    ) -> Dict[str, int]:
        api_seen = 0
        eligible_found = 0
        inserted = 0
        updated = 0
        no_change = 0
        filtered_out = 0
        failed = 0

        for bill in api_client.iter_bills():
            api_seen += 1
            external_bill_id = bill["external_bill_id"]
            proposal_date = bill.get("proposal_date")

            allowed, reason = self._is_collect_target_bill(
                proposal_date=proposal_date,
                min_proposal_date=min_proposal_date,
            )

            if not allowed:
                filtered_out += 1
                print(
                    f"[BATCH][COLLECT] 수집 제외 api_seen={api_seen} external_bill_id={external_bill_id} "
                    f"proposal_date={proposal_date} reason={reason}",
                    flush=True,
                )
                continue

            eligible_found += 1

            try:
                bill_id, action = bill_repo.upsert_bill(bill)

                if action in {"INSERT", "UPDATE"}:
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
                    no_change += 1

                self._commit(batch_repo.conn)
                print(
                    f"[BATCH][COLLECT] 수집 성공 external_bill_id={external_bill_id} action={action}",
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
                print(f"[BATCH][COLLECT][오류] external_bill_id={external_bill_id} error={exc}", flush=True)

        return {
            "api_seen": api_seen,
            "eligible_found": eligible_found,
            "inserted": inserted,
            "updated": updated,
            "no_change": no_change,
            "filtered_out": filtered_out,
            "failed": failed,
        }

    def _sync_members(
        self,
        batch_repo: BatchRepository,
        member_repo: MemberRepository,
        assembly_client: AssemblyOpenApiClient,
        batch_run_id: int,
    ) -> Dict[str, int]:
        fetched = 0
        inserted = 0
        updated = 0
        no_change = 0
        failed = 0

        for member in assembly_client.iter_members(age=self.settings.bill_batch_member_age):
            fetched += 1
            external_member_id = member["external_member_id"]

            try:
                member_id, action = member_repo.upsert_member(member)

                batch_repo.add_item(
                    batch_run_id=batch_run_id,
                    item_type="MEMBER_SYNC",
                    target_external_id=external_member_id,
                    target_internal_id=member_id,
                    item_status="SUCCESS",
                    action_type=action,
                    payload={
                        "name": member.get("name"),
                        "party_name": member.get("party_name"),
                    },
                )

                if action == "INSERT":
                    inserted += 1
                elif action == "UPDATE":
                    updated += 1
                else:
                    no_change += 1

                self._commit(batch_repo.conn)
                print(
                    f"[BATCH][MEMBER] 동기화 성공 external_member_id={external_member_id} action={action}",
                    flush=True,
                )

            except Exception as exc:
                failed += 1
                self._rollback(batch_repo.conn)
                batch_repo.add_item(
                    batch_run_id=batch_run_id,
                    item_type="MEMBER_SYNC",
                    target_external_id=external_member_id,
                    target_internal_id=None,
                    item_status="FAILED",
                    action_type=None,
                    error_message=str(exc),
                    payload={
                        "name": member.get("name"),
                        "party_name": member.get("party_name"),
                    },
                )
                self._commit(batch_repo.conn)
                print(f"[BATCH][MEMBER][오류] external_member_id={external_member_id} error={exc}", flush=True)

        return {
            "fetched": fetched,
            "inserted": inserted,
            "updated": updated,
            "no_change": no_change,
            "failed": failed,
        }

    def _sync_votes(
        self,
        batch_repo: BatchRepository,
        bill_repo: BillRepository,
        member_repo: MemberRepository,
        vote_repo: VoteRepository,
        assembly_client: AssemblyOpenApiClient,
        batch_run_id: int,
        min_proposal_date: date,
    ) -> Dict[str, int]:
        target_bills = bill_repo.get_vote_target_bills(
            limit=self.settings.bill_batch_vote_sync_max_target_bills,
            min_proposal_date=min_proposal_date,
            lookback_days=self.settings.bill_batch_vote_sync_lookback_days,
            missing_only=self.settings.bill_batch_vote_sync_missing_only,
        )

        lookup_maps = member_repo.build_lookup_maps()

        target_bill_count = len(target_bills)
        bills_with_no_vote = 0
        vote_rows_seen = 0
        inserted = 0
        updated = 0
        no_change = 0
        failed = 0

        print(f"[BATCH][VOTE] 표결 수집 대상 의안 수={target_bill_count}", flush=True)

        for bill in target_bills:
            bill_id = bill["id"]
            external_bill_id = bill["external_bill_id"]

            try:
                votes = assembly_client.fetch_votes_for_bill(
                    external_bill_id=external_bill_id,
                    age=self.settings.bill_batch_member_age,
                )

                if not votes:
                    bills_with_no_vote += 1
                    batch_repo.add_item(
                        batch_run_id=batch_run_id,
                        item_type="VOTE_SYNC",
                        target_external_id=external_bill_id,
                        target_internal_id=bill_id,
                        item_status="SKIPPED",
                        action_type="NO_DATA",
                        payload={
                            "official_title": bill.get("official_title"),
                            "proposal_date": str(bill.get("proposal_date")) if bill.get("proposal_date") else None,
                        },
                    )
                    self._commit(batch_repo.conn)
                    print(f"[BATCH][VOTE] 표결 없음 external_bill_id={external_bill_id}", flush=True)
                    continue

                bill_inserted = 0
                bill_updated = 0
                bill_no_change = 0

                for vote in votes:
                    vote_rows_seen += 1

                    member_id = member_repo.resolve_member_id(
                        lookup_maps=lookup_maps,
                        member_no=vote.get("member_no"),
                        mona_cd=vote.get("mona_cd"),
                        member_name=vote.get("member_name"),
                        party_name=vote.get("party_name_snapshot"),
                    )

                    external_vote_key = vote_repo.build_external_vote_key(
                        external_bill_id=external_bill_id,
                        member_no=vote.get("member_no"),
                        mona_cd=vote.get("mona_cd"),
                        member_name=vote.get("member_name"),
                        vote_date=vote.get("vote_date"),
                        vote_result=vote.get("vote_result"),
                    )

                    row = {
                        "bill_id": bill_id,
                        "member_id": member_id,
                        "external_vote_key": external_vote_key,
                        "member_name": vote.get("member_name"),
                        "member_no": vote.get("member_no"),
                        "mona_cd": vote.get("mona_cd"),
                        "party_name_snapshot": vote.get("party_name_snapshot"),
                        "district_name_snapshot": vote.get("district_name_snapshot"),
                        "committee_name_snapshot": vote.get("committee_name_snapshot"),
                        "vote_result": vote.get("vote_result"),
                        "vote_date": vote.get("vote_date"),
                        "source_payload": vote.get("source_payload", {}),
                    }

                    _, action = vote_repo.upsert_vote(row)

                    if action == "INSERT":
                        bill_inserted += 1
                        inserted += 1
                    elif action == "UPDATE":
                        bill_updated += 1
                        updated += 1
                    else:
                        bill_no_change += 1
                        no_change += 1

                batch_repo.add_item(
                    batch_run_id=batch_run_id,
                    item_type="VOTE_SYNC",
                    target_external_id=external_bill_id,
                    target_internal_id=bill_id,
                    item_status="SUCCESS",
                    action_type="SYNC",
                    payload={
                        "official_title": bill.get("official_title"),
                        "proposal_date": str(bill.get("proposal_date")) if bill.get("proposal_date") else None,
                        "vote_count": len(votes),
                        "inserted": bill_inserted,
                        "updated": bill_updated,
                        "no_change": bill_no_change,
                    },
                )
                self._commit(batch_repo.conn)

                print(
                    f"[BATCH][VOTE] 표결 동기화 성공 external_bill_id={external_bill_id} "
                    f"vote_count={len(votes)} inserted={bill_inserted} updated={bill_updated} no_change={bill_no_change}",
                    flush=True,
                )

            except Exception as exc:
                failed += 1
                self._rollback(batch_repo.conn)
                batch_repo.add_item(
                    batch_run_id=batch_run_id,
                    item_type="VOTE_SYNC",
                    target_external_id=external_bill_id,
                    target_internal_id=bill_id,
                    item_status="FAILED",
                    action_type=None,
                    error_message=str(exc),
                    payload={
                        "official_title": bill.get("official_title"),
                        "proposal_date": str(bill.get("proposal_date")) if bill.get("proposal_date") else None,
                    },
                )
                self._commit(batch_repo.conn)
                print(f"[BATCH][VOTE][오류] external_bill_id={external_bill_id} error={exc}", flush=True)

        return {
            "target_bills": target_bill_count,
            "bills_with_no_vote": bills_with_no_vote,
            "vote_rows_seen": vote_rows_seen,
            "inserted": inserted,
            "updated": updated,
            "no_change": no_change,
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
            print(f"[BATCH][AI] 장시간 처리중 상태 초기화 count={stale_reset}", flush=True)

        candidate_bills = bill_repo.get_pending_ai_bills(
            limit=self.settings.bill_batch_max_ai_per_run,
            min_proposal_date=min_proposal_date,
        )
        category_map = category_repo.get_active_category_map()

        print(
            f"[BATCH][AI] 처리 대상 건수={len(candidate_bills)} 일일 처리 한도={self.settings.bill_batch_max_ai_per_run}",
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
                            "reason": "현재 분석본과 AI 입력 해시가 동일하여 재분석을 생략했습니다",
                            "analysis_id": current_analysis.get("id"),
                        },
                    )
                    self._commit(batch_repo.conn)
                    skipped += 1
                    print(
                        f"[BATCH][AI] 재분석 생략 external_bill_id={external_bill_id} reason=현재 분석본과 AI 입력 해시 동일",
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
                        message=f"AI 결과 카테고리를 내부 카테고리로 매핑할 수 없습니다: {ai_output['categories']}",
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
                print(f"[BATCH][AI] 분석 성공 external_bill_id={external_bill_id} analysis_id={analysis_id}", flush=True)

            except GeminiApiError as exc:
                self._rollback(batch_repo.conn)

                retry_count_after_update = int(bill.get("ai_retry_count", 0)) + 1

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
                            "reason": "Gemini 할당량이 소진되어 이번 실행의 AI 처리를 중단했습니다",
                            "error_code": exc.code,
                            "retryable": exc.retryable,
                            "quota_exhausted": exc.quota_exhausted,
                        },
                    )
                    self._commit(batch_repo.conn)
                    failed += 1
                    quota_exhausted = True
                    print(f"[BATCH][AI] Gemini 할당량 소진으로 AI 큐 처리를 중단합니다 external_bill_id={external_bill_id}", flush=True)
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
                        "error_code": exc.code,
                        "retryable": exc.retryable,
                        "quota_exhausted": exc.quota_exhausted,
                    },
                )
                self._commit(batch_repo.conn)
                failed += 1
                print(f"[BATCH][AI][오류] external_bill_id={external_bill_id} error={exc}", flush=True)

            except Exception as exc:
                self._rollback(batch_repo.conn)

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
                        "error_code": "UNEXPECTED_ERROR",
                    },
                )
                self._commit(batch_repo.conn)
                failed += 1
                print(f"[BATCH][AI][오류] external_bill_id={external_bill_id} error={exc}", flush=True)

        return {
            "attempted": attempted,
            "success": success,
            "skipped": skipped,
            "failed": failed,
            "quota_exhausted": quota_exhausted,
        }

    def _build_run_message(
        self,
        collect_result: Dict[str, int] | None,
        member_result: Dict[str, int] | None,
        vote_result: Dict[str, int] | None,
        ai_result: Dict[str, Any] | None,
    ) -> str:
        parts = []

        if collect_result is not None:
            parts.append(
                "수집 결과("
                f"API조회={collect_result['api_seen']}, "
                f"수집대상={collect_result['eligible_found']}, "
                f"신규저장={collect_result['inserted']}, "
                f"갱신={collect_result['updated']}, "
                f"변경없음={collect_result['no_change']}, "
                f"수집제외={collect_result['filtered_out']}, "
                f"실패={collect_result['failed']}"
                ")"
            )

        if member_result is not None:
            parts.append(
                "의원 동기화("
                f"조회={member_result['fetched']}, "
                f"신규={member_result['inserted']}, "
                f"갱신={member_result['updated']}, "
                f"변경없음={member_result['no_change']}, "
                f"실패={member_result['failed']}"
                ")"
            )

        if vote_result is not None:
            parts.append(
                "표결 동기화("
                f"대상의안={vote_result['target_bills']}, "
                f"표결없음={vote_result['bills_with_no_vote']}, "
                f"표결행조회={vote_result['vote_rows_seen']}, "
                f"신규={vote_result['inserted']}, "
                f"갱신={vote_result['updated']}, "
                f"변경없음={vote_result['no_change']}, "
                f"실패={vote_result['failed']}"
                ")"
            )

        if ai_result is not None:
            parts.append(
                "AI 결과("
                f"시도={ai_result['attempted']}, "
                f"성공={ai_result['success']}, "
                f"생략={ai_result['skipped']}, "
                f"실패={ai_result['failed']}, "
                f"할당량소진={ai_result['quota_exhausted']}"
                ")"
            )

        return " ".join(parts)

    def _commit(self, conn):
        conn.commit()

    def _rollback(self, conn):
        conn.rollback()

    def run(self, trigger_type: str | None = None):
        trigger_type = trigger_type or self.settings.bill_batch_trigger_type
        min_proposal_date = datetime.strptime(self.settings.min_proposal_date, "%Y-%m-%d").date()

        print(
            f"[BATCH] 배치를 시작합니다 trigger_type={trigger_type} "
            f"min_proposal_date={min_proposal_date} "
            f"enable_bill_collect={self.settings.bill_batch_enable_bill_collect} "
            f"enable_member_sync={self.settings.bill_batch_enable_member_sync} "
            f"enable_vote_sync={self.settings.bill_batch_enable_vote_sync} "
            f"enable_ai={self.settings.bill_batch_enable_ai}",
            flush=True,
        )

        conn = get_connection(self.settings)
        conn.autocommit = False

        batch_run_id = None

        try:
            batch_repo = BatchRepository(conn, self.settings.db_schema)
            bill_repo = BillRepository(conn, self.settings.db_schema)
            member_repo = MemberRepository(conn, self.settings.db_schema)
            vote_repo = VoteRepository(conn, self.settings.db_schema)
            ai_repo = AiRepository(conn, self.settings.db_schema)
            category_repo = CategoryRepository(conn, self.settings.db_schema)

            bill_api_client = BillInfoApiClient(self.settings)
            assembly_client = AssemblyOpenApiClient(self.settings)
            gemini_client = GeminiClient(self.settings) if self.settings.bill_batch_enable_ai else None

            batch_run_id = batch_repo.create_run(
                job_type=self.settings.bill_batch_job_type,
                trigger_type=trigger_type,
                image_tag=self.settings.image_tag,
                git_commit_sha=self.settings.git_commit_sha,
            )
            self._commit(conn)
            print(f"[BATCH] batch_run_id={batch_run_id} 실행 이력을 생성했습니다", flush=True)

            collect_result = None
            member_result = None
            vote_result = None
            ai_result = None

            if self.settings.bill_batch_enable_bill_collect:
                collect_result = self._collect_bills(
                    batch_repo=batch_repo,
                    bill_repo=bill_repo,
                    api_client=bill_api_client,
                    batch_run_id=batch_run_id,
                    min_proposal_date=min_proposal_date,
                )

            if self.settings.bill_batch_enable_member_sync:
                member_result = self._sync_members(
                    batch_repo=batch_repo,
                    member_repo=member_repo,
                    assembly_client=assembly_client,
                    batch_run_id=batch_run_id,
                )

            if self.settings.bill_batch_enable_vote_sync:
                vote_result = self._sync_votes(
                    batch_repo=batch_repo,
                    bill_repo=bill_repo,
                    member_repo=member_repo,
                    vote_repo=vote_repo,
                    assembly_client=assembly_client,
                    batch_run_id=batch_run_id,
                    min_proposal_date=min_proposal_date,
                )

            if self.settings.bill_batch_enable_ai:
                ai_result = self._process_ai_queue(
                    batch_repo=batch_repo,
                    bill_repo=bill_repo,
                    ai_repo=ai_repo,
                    category_repo=category_repo,
                    gemini_client=gemini_client,
                    batch_run_id=batch_run_id,
                    min_proposal_date=min_proposal_date,
                )

            total_failed = 0
            if collect_result:
                total_failed += collect_result["failed"]
            if member_result:
                total_failed += member_result["failed"]
            if vote_result:
                total_failed += vote_result["failed"]
            if ai_result:
                total_failed += ai_result["failed"]

            quota_exhausted = ai_result["quota_exhausted"] if ai_result else False

            if total_failed == 0 and not quota_exhausted:
                run_status = "SUCCESS"
            else:
                run_status = "PARTIAL_SUCCESS"

            message = self._build_run_message(
                collect_result=collect_result,
                member_result=member_result,
                vote_result=vote_result,
                ai_result=ai_result,
            )

            records_found = 0
            records_inserted = 0
            records_updated = 0
            records_skipped = 0

            if collect_result:
                records_found += collect_result["eligible_found"]
                records_inserted += collect_result["inserted"]
                records_updated += collect_result["updated"]
                records_skipped += collect_result["filtered_out"] + collect_result["no_change"]

            if member_result:
                records_found += member_result["fetched"]
                records_inserted += member_result["inserted"]
                records_updated += member_result["updated"]
                records_skipped += member_result["no_change"]

            if vote_result:
                records_found += vote_result["vote_rows_seen"]
                records_inserted += vote_result["inserted"]
                records_updated += vote_result["updated"]
                records_skipped += vote_result["no_change"] + vote_result["bills_with_no_vote"]

            if ai_result:
                records_found += ai_result["attempted"]
                records_skipped += ai_result["skipped"]

            batch_repo.finish_run(
                batch_run_id=batch_run_id,
                run_status=run_status,
                records_found=records_found,
                records_inserted=records_inserted,
                records_updated=records_updated,
                records_skipped=records_skipped,
                records_failed=total_failed,
                message=message,
            )
            self._commit(conn)

            result = {
                "batch_run_id": batch_run_id,
                "run_status": run_status,
                "collect": collect_result,
                "member": member_result,
                "vote": vote_result,
                "ai": ai_result,
                "message": message,
            }
            print(f"[BATCH] 배치가 종료되었습니다 result={result}", flush=True)
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
                        message=f"배치 실행 중 오류가 발생했습니다: {exc}",
                    )
                    self._commit(conn)
                except Exception:
                    self._rollback(conn)

            raise

        finally:
            conn.close()