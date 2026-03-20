from datetime import datetime
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

        now = datetime.utcnow()

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

    def get_summary_hash(self, bill_id: int):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT summary_raw_hash
                FROM {self.schema}.bills
                WHERE id = %s
                """,
                (bill_id,),
            )
            row = cur.fetchone()
            return row["summary_raw_hash"] if row else None