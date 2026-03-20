from datetime import date
from typing import Any, Dict, List, Tuple

from psycopg2.extras import Json

from app.utils.hash_utils import sha256_hex


class BillRepository:
    def __init__(self, conn, schema: str):
        self.conn = conn
        self.schema = schema

    def _find_by_external_bill_id(self, external_bill_id: str):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT *
                FROM {self.schema}.bills
                WHERE external_bill_id = %s
                """,
                (external_bill_id,),
            )
            return cur.fetchone()

    def upsert_bill(self, bill: Dict[str, Any]) -> Tuple[int, str]:
        existing = self._find_by_external_bill_id(bill["external_bill_id"])
        summary_hash = sha256_hex(bill.get("summary_raw"))

        comparable = {
            "bill_no": bill.get("bill_no"),
            "official_title": bill.get("official_title"),
            "proposal_date": bill.get("proposal_date"),
            "proposer_kind": bill.get("proposer_kind"),
            "representative_proposer_name": bill.get("representative_proposer_name"),
            "proposer_count": bill.get("proposer_count", 0),
            "summary_raw": bill.get("summary_raw"),
            "summary_raw_hash": summary_hash,
            "detail_url": bill.get("detail_url"),
            "current_proc_stage_code": bill.get("current_proc_stage_code"),
            "current_proc_stage_name": bill.get("current_proc_stage_name"),
            "current_proc_stage_order": bill.get("current_proc_stage_order"),
            "current_pass_gubn": bill.get("current_pass_gubn"),
            "current_general_result": bill.get("current_general_result"),
        }

        if not existing:
            with self.conn.cursor() as cur:
                cur.execute(
                    f"""
                    INSERT INTO {self.schema}.bills (
                        external_bill_id,
                        bill_no,
                        official_title,
                        proposal_date,
                        proposer_kind,
                        representative_proposer_name,
                        proposer_count,
                        summary_raw,
                        summary_raw_hash,
                        detail_url,
                        current_proc_stage_code,
                        current_proc_stage_name,
                        current_proc_stage_order,
                        current_pass_gubn,
                        current_general_result,
                        ai_status,
                        ai_retry_count,
                        last_ai_attempt_at,
                        next_ai_retry_at,
                        last_ai_error_code,
                        last_ai_error_message,
                        last_ai_analysis_id,
                        source_payload,
                        first_collected_at,
                        last_collected_at,
                        last_status_changed_at,
                        created_at,
                        updated_at
                    ) VALUES (
                        %s, %s, %s, %s, %s, %s, %s,
                        %s, %s, %s,
                        %s, %s, %s, %s, %s,
                        'PENDING', 0, NULL, NULL, NULL, NULL, NULL,
                        %s,
                        now(), now(), now(), now(), now()
                    )
                    RETURNING id
                    """,
                    (
                        bill["external_bill_id"],
                        bill.get("bill_no"),
                        bill["official_title"],
                        bill.get("proposal_date"),
                        bill.get("proposer_kind"),
                        bill.get("representative_proposer_name"),
                        bill.get("proposer_count", 0),
                        bill.get("summary_raw"),
                        summary_hash,
                        bill.get("detail_url"),
                        bill.get("current_proc_stage_code"),
                        bill.get("current_proc_stage_name"),
                        bill.get("current_proc_stage_order"),
                        bill.get("current_pass_gubn"),
                        bill.get("current_general_result"),
                        Json(bill.get("source_payload", {})),
                    ),
                )
                bill_id = cur.fetchone()["id"]
            return bill_id, "INSERT"

        status_changed = any(
            [
                existing.get("current_proc_stage_code") != bill.get("current_proc_stage_code"),
                existing.get("current_proc_stage_name") != bill.get("current_proc_stage_name"),
                existing.get("current_proc_stage_order") != bill.get("current_proc_stage_order"),
                existing.get("current_pass_gubn") != bill.get("current_pass_gubn"),
                existing.get("current_general_result") != bill.get("current_general_result"),
            ]
        )

        ai_input_changed = any(
            [
                existing.get("official_title") != bill.get("official_title"),
                existing.get("summary_raw_hash") != summary_hash,
            ]
        )

        no_change = all(existing.get(key) == value for key, value in comparable.items())
        if no_change:
            with self.conn.cursor() as cur:
                cur.execute(
                    f"""
                    UPDATE {self.schema}.bills
                    SET last_collected_at = now(),
                        updated_at = now()
                    WHERE id = %s
                    """,
                    (existing["id"],),
                )
            return existing["id"], "NO_CHANGE"

        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET
                    bill_no = %s,
                    official_title = %s,
                    proposal_date = %s,
                    proposer_kind = %s,
                    representative_proposer_name = %s,
                    proposer_count = %s,
                    summary_raw = %s,
                    summary_raw_hash = %s,
                    detail_url = %s,
                    current_proc_stage_code = %s,
                    current_proc_stage_name = %s,
                    current_proc_stage_order = %s,
                    current_pass_gubn = %s,
                    current_general_result = %s,
                    source_payload = %s,
                    ai_status = CASE WHEN %s THEN 'PENDING' ELSE ai_status END,
                    ai_retry_count = CASE WHEN %s THEN 0 ELSE ai_retry_count END,
                    last_ai_attempt_at = CASE WHEN %s THEN NULL ELSE last_ai_attempt_at END,
                    next_ai_retry_at = CASE WHEN %s THEN NULL ELSE next_ai_retry_at END,
                    last_ai_error_code = CASE WHEN %s THEN NULL ELSE last_ai_error_code END,
                    last_ai_error_message = CASE WHEN %s THEN NULL ELSE last_ai_error_message END,
                    last_ai_analysis_id = CASE WHEN %s THEN NULL ELSE last_ai_analysis_id END,
                    last_collected_at = now(),
                    last_status_changed_at = CASE WHEN %s THEN now() ELSE last_status_changed_at END,
                    updated_at = now()
                WHERE id = %s
                """,
                (
                    bill.get("bill_no"),
                    bill["official_title"],
                    bill.get("proposal_date"),
                    bill.get("proposer_kind"),
                    bill.get("representative_proposer_name"),
                    bill.get("proposer_count", 0),
                    bill.get("summary_raw"),
                    summary_hash,
                    bill.get("detail_url"),
                    bill.get("current_proc_stage_code"),
                    bill.get("current_proc_stage_name"),
                    bill.get("current_proc_stage_order"),
                    bill.get("current_pass_gubn"),
                    bill.get("current_general_result"),
                    Json(bill.get("source_payload", {})),
                    ai_input_changed,
                    ai_input_changed,
                    ai_input_changed,
                    ai_input_changed,
                    ai_input_changed,
                    ai_input_changed,
                    ai_input_changed,
                    status_changed,
                    existing["id"],
                ),
            )
        return existing["id"], "UPDATE"

    def replace_proposers(self, bill_id: int, bill: Dict[str, Any]):
        proposer_name = (bill.get("representative_proposer_name") or "").strip()
        proposer_kind = (bill.get("proposer_kind") or "").strip()

        with self.conn.cursor() as cur:
            cur.execute(
                f"DELETE FROM {self.schema}.bill_proposers WHERE bill_id = %s",
                (bill_id,),
            )

            if proposer_name:
                cur.execute(
                    f"""
                    INSERT INTO {self.schema}.bill_proposers (
                        bill_id,
                        proposer_name,
                        proposer_type,
                        is_representative,
                        display_order,
                        source_payload,
                        created_at
                    ) VALUES (%s, %s, %s, TRUE, 1, %s, now())
                    """,
                    (
                        bill_id,
                        proposer_name,
                        proposer_kind or None,
                        Json(
                            {
                                "note": "1차 배치에서는 대표 발의자 1명 기준 저장",
                                "raw": bill.get("source_payload", {}),
                            }
                        ),
                    ),
                )

    def insert_status_history_if_needed(self, bill_id: int, bill: Dict[str, Any]):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                INSERT INTO {self.schema}.bill_status_history (
                    bill_id,
                    proc_stage_code,
                    proc_stage_name,
                    proc_stage_order,
                    pass_gubn,
                    general_result,
                    proc_date,
                    observed_at,
                    status_payload
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, now(), %s
                )
                ON CONFLICT DO NOTHING
                """,
                (
                    bill_id,
                    bill.get("current_proc_stage_code"),
                    bill.get("current_proc_stage_name"),
                    bill.get("current_proc_stage_order"),
                    bill.get("current_pass_gubn"),
                    bill.get("current_general_result"),
                    bill.get("status_proc_date"),
                    Json(bill.get("source_payload", {})),
                ),
            )

    def reset_stale_processing(self, timeout_minutes: int) -> int:
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET
                    ai_status = 'RETRY_WAIT',
                    next_ai_retry_at = now(),
                    updated_at = now()
                WHERE ai_status = 'PROCESSING'
                  AND last_ai_attempt_at IS NOT NULL
                  AND last_ai_attempt_at < now() - (%s || ' minutes')::interval
                """,
                (timeout_minutes,),
            )
            return cur.rowcount

    def get_pending_ai_bills(self, limit: int, min_proposal_date: date) -> List[Dict[str, Any]]:
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT *
                FROM {self.schema}.bills
                WHERE proposal_date IS NOT NULL
                  AND proposal_date >= %s
                  AND ai_status IN ('PENDING', 'RETRY_WAIT')
                  AND (next_ai_retry_at IS NULL OR next_ai_retry_at <= now())
                ORDER BY proposal_date ASC, id ASC
                LIMIT %s
                """,
                (min_proposal_date, limit),
            )
            return cur.fetchall()

    def mark_ai_processing(self, bill_id: int):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET
                    ai_status = 'PROCESSING',
                    last_ai_attempt_at = now(),
                    updated_at = now()
                WHERE id = %s
                """,
                (bill_id,),
            )

    def mark_ai_success(self, bill_id: int, analysis_id: int):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET
                    ai_status = 'SUCCESS',
                    next_ai_retry_at = NULL,
                    last_ai_error_code = NULL,
                    last_ai_error_message = NULL,
                    last_ai_analysis_id = %s,
                    updated_at = now()
                WHERE id = %s
                """,
                (analysis_id, bill_id),
            )

    def mark_ai_retry_wait(self, bill_id: int, error_code: str, error_message: str, wait_minutes: int):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET
                    ai_status = 'RETRY_WAIT',
                    ai_retry_count = ai_retry_count + 1,
                    next_ai_retry_at = now() + (%s || ' minutes')::interval,
                    last_ai_error_code = %s,
                    last_ai_error_message = %s,
                    updated_at = now()
                WHERE id = %s
                """,
                (wait_minutes, error_code, error_message[:5000], bill_id),
            )

    def mark_ai_permanent_failed(self, bill_id: int, error_code: str, error_message: str):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET
                    ai_status = 'PERMANENT_FAILED',
                    ai_retry_count = ai_retry_count + 1,
                    next_ai_retry_at = NULL,
                    last_ai_error_code = %s,
                    last_ai_error_message = %s,
                    updated_at = now()
                WHERE id = %s
                """,
                (error_code, error_message[:5000], bill_id),
            )

    def mark_ai_skipped(self, bill_id: int, analysis_id: int | None):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET
                    ai_status = 'SUCCESS',
                    next_ai_retry_at = NULL,
                    last_ai_error_code = NULL,
                    last_ai_error_message = NULL,
                    last_ai_analysis_id = COALESCE(%s, last_ai_analysis_id),
                    updated_at = now()
                WHERE id = %s
                """,
                (analysis_id, bill_id),
            )