from datetime import date, timedelta
from typing import Any, Dict

from app.clients.bill_info_api_client import BillInfoApiClient
from app.clients.gemini_client import GeminiClient
from app.config import Settings
from app.db import get_connection
from app.repositories.ai_repository import AiRepository
from app.repositories.batch_repository import BatchRepository
from app.repositories.bill_repository import BillRepository
from app.repositories.category_repository import CategoryRepository


class BatchRunner:
    def __init__(self, settings: Settings):
        self.settings = settings

    def _resolve_dates(self, from_date: date | None, to_date: date | None):
        if from_date and to_date:
            return from_date, to_date

        yesterday = date.today() - timedelta(days=1)
        if to_date is None:
            to_date = yesterday

        if from_date is None:
            from_date = to_date - timedelta(days=self.settings.bill_batch_lookback_days - 1)

        return from_date, to_date

    def run(self, from_date: date | None = None, to_date: date | None = None, trigger_type: str | None = None):
        trigger_type = trigger_type or self.settings.bill_batch_trigger_type
        start_date, end_date = self._resolve_dates(from_date, to_date)

        conn = get_connection(self.settings)
        conn.autocommit = False

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
            conn.commit()

            found = inserted = updated = skipped = failed = 0

            category_map = category_repo.get_active_category_map()
            bills = api_client.fetch_bills(start_date, end_date)
            found = len(bills)

            for bill in bills:
                external_bill_id = bill["external_bill_id"]
                bill_id = None

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

                    summary_hash = bill_repo.get_summary_hash(bill_id)
                    current_analysis = ai_repo.get_current_analysis(bill_id)

                    if current_analysis and current_analysis.get("source_text_hash") == summary_hash:
                        batch_repo.add_item(
                            batch_run_id=batch_run_id,
                            item_type="BILL_AI_ANALYZE",
                            target_external_id=external_bill_id,
                            target_internal_id=bill_id,
                            item_status="SKIPPED",
                            action_type="NO_CHANGE",
                            payload={
                                "reason": "same_source_hash",
                                "analysis_id": current_analysis.get("id"),
                            },
                        )
                        skipped += 1
                        conn.commit()
                        continue

                    ai_input = {
                        "official_title": bill.get("official_title"),
                        "summary_raw": bill.get("summary_raw") or "",
                    }

                    ai_output = gemini_client.analyze_bill(
                        title=ai_input["official_title"],
                        body=ai_input["summary_raw"],
                    )

                    category_ids = []
                    for code in ai_output["categories"]:
                        category_id = category_map.get(code)
                        if category_id:
                            category_ids.append(category_id)

                    if not category_ids:
                        raise ValueError(f"No valid category mapped from AI output: {ai_output['categories']}")

                    analysis_id = ai_repo.insert_success_analysis(
                        bill_id=bill_id,
                        source_text_hash=summary_hash,
                        model_name=self.settings.gemini_model,
                        prompt_version=self.settings.prompt_version,
                        temperature=self.settings.gemini_temperature,
                        analysis_input=ai_input,
                        analysis_output=ai_output,
                        category_ids=category_ids,
                        axis_for=ai_output["vote"].get("for", {}),
                        axis_against=ai_output["vote"].get("against", {}),
                    )

                    batch_repo.add_item(
                        batch_run_id=batch_run_id,
                        item_type="BILL_AI_ANALYZE",
                        target_external_id=external_bill_id,
                        target_internal_id=bill_id,
                        item_status="SUCCESS",
                        action_type="INSERT",
                        payload={
                            "analysis_id": analysis_id,
                            "categories": ai_output["categories"],
                        },
                    )

                    conn.commit()

                except Exception as exc:
                    failed += 1
                    try:
                        if bill_id:
                            ai_input = {
                                "official_title": bill.get("official_title"),
                                "summary_raw": bill.get("summary_raw") or "",
                            }
                            summary_hash = bill_repo.get_summary_hash(bill_id)
                            ai_repo.insert_failed_analysis(
                                bill_id=bill_id,
                                source_text_hash=summary_hash,
                                model_name=self.settings.gemini_model,
                                prompt_version=self.settings.prompt_version,
                                temperature=self.settings.gemini_temperature,
                                analysis_input=ai_input,
                                error_message=str(exc),
                            )

                        batch_repo.add_item(
                            batch_run_id=batch_run_id,
                            item_type="BILL_PROCESS",
                            target_external_id=external_bill_id,
                            target_internal_id=bill_id,
                            item_status="FAILED",
                            action_type=None,
                            error_message=str(exc),
                            payload={
                                "official_title": bill.get("official_title"),
                            },
                        )
                        conn.commit()
                    except Exception:
                        conn.rollback()

            if failed == 0:
                run_status = "SUCCESS"
            elif found > failed:
                run_status = "PARTIAL_SUCCESS"
            else:
                run_status = "FAILED"

            batch_repo.finish_run(
                batch_run_id=batch_run_id,
                run_status=run_status,
                records_found=found,
                records_inserted=inserted,
                records_updated=updated,
                records_skipped=skipped,
                records_failed=failed,
                message=f"bill-batch finished for {start_date} ~ {end_date}",
            )
            conn.commit()

            return {
                "batch_run_id": batch_run_id,
                "run_status": run_status,
                "records_found": found,
                "records_inserted": inserted,
                "records_updated": updated,
                "records_skipped": skipped,
                "records_failed": failed,
                "from_date": str(start_date),
                "to_date": str(end_date),
            }

        finally:
            conn.close()