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

        # summary_raw 가 dict 에 명시되지 않으면(= 신 API 가 제공하지 않으면) 기존 DB 값 유지.
        # 신 TVBPMBILL11 응답에는 summary 가 없어 빈 값으로 덮어쓰지 않는다 (D1 정책).
        has_summary_field = "summary_raw" in bill
        summary_hash = sha256_hex(bill.get("summary_raw")) if has_summary_field else None

        comparable = {
            "bill_no": bill.get("bill_no"),
            "official_title": bill.get("official_title"),
            "proposal_date": bill.get("proposal_date"),
            "proposer_kind": bill.get("proposer_kind"),
            "representative_proposer_name": bill.get("representative_proposer_name"),
            "proposer_count": bill.get("proposer_count", 0),
            "detail_url": bill.get("detail_url"),
            "current_proc_stage_code": bill.get("current_proc_stage_code"),
            "current_proc_stage_name": bill.get("current_proc_stage_name"),
            "current_proc_stage_order": bill.get("current_proc_stage_order"),
            "current_pass_gubn": bill.get("current_pass_gubn"),
            "current_general_result": bill.get("current_general_result"),
        }
        # summary 필드가 명시된 경우에만 비교 대상에 포함
        if has_summary_field:
            comparable["summary_raw"] = bill.get("summary_raw")
            comparable["summary_raw_hash"] = summary_hash

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

        ai_input_changed = (
            existing.get("official_title") != bill.get("official_title")
            or (has_summary_field and existing.get("summary_raw_hash") != summary_hash)
        )

        no_change = all(existing.get(key) == value for key, value in comparable.items())
        if no_change:
            with self.conn.cursor() as cur:
                cur.execute(
                    f"""
                    UPDATE {self.schema}.bills
                    SET last_collected_at = now()
                    WHERE id = %s
                    """,
                    (existing["id"],),
                )
            return existing["id"], "NO_CHANGE"

        with self.conn.cursor() as cur:
            if ai_input_changed:
                cur.execute(
                    f"""
                    UPDATE {self.schema}.bill_ai_analyses
                    SET is_current = FALSE
                    WHERE bill_id = %s
                      AND is_current = TRUE
                    """,
                    (existing["id"],),
                )

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
                    -- summary 필드가 None 으로 전달되면 기존 DB 값을 유지한다.
                    -- 신 TVBPMBILL11 API 가 summary 를 제공하지 않아도 옛 summary 가 보존된다.
                    summary_raw = COALESCE(%s, summary_raw),
                    summary_raw_hash = COALESCE(%s, summary_raw_hash),
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

    def _resolve_member_id_from_lookup_maps(
        self,
        lookup_maps: Dict[str, Dict[str, int]],
        member_no: str | None,
        mona_cd: str | None,
        member_name: str | None,
        party_name: str | None,
    ) -> int | None:
        member_no = (member_no or "").strip()
        mona_cd = (mona_cd or "").strip()
        member_name = (member_name or "").strip()
        party_name = (party_name or "").strip()

        if member_no and member_no in lookup_maps["by_member_no"]:
            return lookup_maps["by_member_no"][member_no]

        if mona_cd and mona_cd in lookup_maps["by_mona_cd"]:
            return lookup_maps["by_mona_cd"][mona_cd]

        if mona_cd and mona_cd in lookup_maps["by_external_member_id"]:
            return lookup_maps["by_external_member_id"][mona_cd]

        if member_name:
            key = f"{member_name}|{party_name}"
            resolved = lookup_maps["by_name_party"].get(key)
            if resolved:
                return resolved

            fallback_key = f"{member_name}|"
            return lookup_maps["by_name_party"].get(fallback_key)

        return None

    def repair_missing_proposer_member_ids(self, lookup_maps: Dict[str, Dict[str, int]]) -> int:
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT id, proposer_name, proposer_type
                FROM {self.schema}.bill_proposers
                WHERE member_id IS NULL
                ORDER BY id
                """
            )
            rows = cur.fetchall()

        repaired = 0

        with self.conn.cursor() as cur:
            for row in rows:
                member_id = self._resolve_member_id_from_lookup_maps(
                    lookup_maps=lookup_maps,
                    member_no=None,
                    mona_cd=None,
                    member_name=row.get("proposer_name"),
                    party_name=None,
                )

                if not member_id:
                    continue

                cur.execute(
                    f"""
                    UPDATE {self.schema}.bill_proposers
                    SET member_id = %s
                    WHERE id = %s
                      AND member_id IS NULL
                    """,
                    (member_id, row["id"]),
                )

                if cur.rowcount:
                    repaired += 1

        return repaired

    def replace_proposers(self, bill_id: int, bill: Dict[str, Any], member_id: int | None = None):
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
                        member_id,
                        proposer_name,
                        proposer_type,
                        is_representative,
                        display_order,
                        source_payload,
                        created_at
                    ) VALUES (%s, %s, %s, %s, TRUE, 1, %s, now())
                    """,
                    (
                        bill_id,
                        member_id,
                        proposer_name,
                        proposer_kind or None,
                        Json(
                            {
                                "note": "1차 배치에서는 대표 발의자 1명 기준 저장",
                                "member_id_resolved": member_id is not None,
                                "raw": bill.get("source_payload", {}),
                            }
                        ),
                    ),
                )

    def get_bills_pending_proposer_scrape(self, min_proposal_date: date, limit: int) -> List[Dict[str, Any]]:
        # 공동발의자(proposer_count > 1)가 있는데 bill_proposers에 아직 공동발의자 행이
        # 하나도 없는 의안만 대상으로 한다. 공동발의자 명단은 불변 데이터라 한 번 채워지면
        # 다시 재시도 대상이 되지 않는다(요약 스크래핑과 동일한 1회성 패턴).
        with self.conn.cursor() as cur:
            limit_clause = f"LIMIT {limit}" if limit > 0 else ""
            cur.execute(
                f"""
                SELECT b.id, b.external_bill_id, b.representative_proposer_name
                FROM {self.schema}.bills b
                WHERE b.proposal_date >= %s
                  AND b.proposer_count > 1
                  AND NOT EXISTS (
                        SELECT 1
                        FROM {self.schema}.bill_proposers p
                        WHERE p.bill_id = b.id
                          AND p.is_representative = FALSE
                  )
                ORDER BY b.proposal_date DESC, b.id DESC
                {limit_clause}
                """,
                (min_proposal_date,),
            )
            return cur.fetchall()

    def insert_co_proposers(self, bill_id: int, proposers: List[Dict[str, Any]]):
        with self.conn.cursor() as cur:
            display_order = 2
            for proposer in proposers:
                cur.execute(
                    f"""
                    INSERT INTO {self.schema}.bill_proposers (
                        bill_id,
                        member_id,
                        proposer_name,
                        proposer_type,
                        is_representative,
                        display_order,
                        source_payload,
                        created_at
                    ) VALUES (%s, %s, %s, NULL, FALSE, %s, %s, now())
                    """,
                    (
                        bill_id,
                        proposer.get("member_id"),
                        proposer["name"],
                        display_order,
                        Json({"party_name": proposer.get("party_name")}),
                    ),
                )
                display_order += 1

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
                    Json(
                        {
                            "current_proc_stage_code": bill.get("current_proc_stage_code"),
                            "current_proc_stage_name": bill.get("current_proc_stage_name"),
                            "current_proc_stage_order": bill.get("current_proc_stage_order"),
                            "current_pass_gubn": bill.get("current_pass_gubn"),
                            "current_general_result": bill.get("current_general_result"),
                            "status_proc_date": str(bill.get("status_proc_date")) if bill.get("status_proc_date") else None,
                            "source_payload": bill.get("source_payload", {}),
                        }
                    ),
                ),
            )

    def insert_status_history_entries(self, bill_id: int, entries: List[Dict[str, Any]]) -> int:
        """
        신 API (TVBPMBILL11) 의 단계별 날짜로부터 추출된 entries 를 일괄 INSERT.

        - 각 entry: {stage_code, stage_name, stage_order, pass_gubn, general_result, proc_date}
        - bill_status_history 의 unique index (bill_id, stage_code, pass_gubn, general_result, proc_date)
          가 멱등성을 보장. 같은 단계가 이미 있으면 ON CONFLICT DO NOTHING.
        - 반환: 새로 삽입된 row 수.
        """
        if not entries:
            return 0

        inserted = 0
        with self.conn.cursor() as cur:
            for entry in entries:
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
                        entry.get("stage_code"),
                        entry.get("stage_name"),
                        entry.get("stage_order"),
                        entry.get("pass_gubn"),
                        entry.get("general_result"),
                        entry.get("proc_date"),
                        Json({
                            "stage_code": entry.get("stage_code"),
                            "stage_name": entry.get("stage_name"),
                            "stage_order": entry.get("stage_order"),
                            "pass_gubn": entry.get("pass_gubn"),
                            "general_result": entry.get("general_result"),
                            "proc_date": str(entry.get("proc_date")) if entry.get("proc_date") else None,
                        }),
                    ),
                )
                if cur.rowcount:
                    inserted += 1
        return inserted

    def get_bills_pending_scrape(self, min_proposal_date: date, limit: int) -> List[Dict[str, Any]]:
        # summary_raw IS NULL  → 아직 스크래핑 시도 안 된 의안
        # summary_raw = ''     → 스크래핑 시도했으나 LIKMS에 내용 없음 (mark_scrape_no_content)
        # 후자는 재시도 대상에서 제외한다.
        with self.conn.cursor() as cur:
            params: tuple = (min_proposal_date,)
            limit_clause = f"LIMIT {limit}" if limit > 0 else ""
            cur.execute(
                f"""
                SELECT id, external_bill_id
                FROM {self.schema}.bills
                WHERE proposal_date >= %s
                  AND summary_raw IS NULL
                ORDER BY proposal_date DESC, id DESC
                {limit_clause}
                """,
                params,
            )
            return cur.fetchall()

    def update_summary_raw(self, bill_id: int, summary_raw: str):
        # summary_raw를 새로 채웠으면 ai_status를 PENDING으로 초기화해
        # 이전에 빈 내용으로 AI가 처리됐거나(SUCCESS/PERMANENT_FAILED) 실패했더라도 재분석 대상이 된다.
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET summary_raw = %s,
                    summary_raw_hash = %s,
                    ai_status = 'PENDING',
                    ai_retry_count = 0,
                    next_ai_retry_at = NULL,
                    updated_at = now()
                WHERE id = %s
                """,
                (summary_raw, sha256_hex(summary_raw), bill_id),
            )

    def mark_scrape_no_content(self, bill_id: int):
        # LIKMS에 해당 의안의 #prntSummary가 없음이 확인된 경우 빈 문자열('')로 마킹.
        # get_bills_pending_scrape는 summary_raw IS NULL 만 조회하므로 이 의안은 이후 스크래핑 대상에서 제외된다.
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET summary_raw = '',
                    updated_at = now()
                WHERE id = %s
                """,
                (bill_id,),
            )

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
                  AND summary_raw IS NOT NULL
                  AND summary_raw != ''
                ORDER BY proposal_date DESC, id DESC
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
                SET ai_status = 'PROCESSING',
                    ai_retry_count = ai_retry_count + 1,
                    last_ai_attempt_at = now(),
                    next_ai_retry_at = NULL,
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
                SET ai_status = 'SUCCESS',
                    last_ai_analysis_id = %s,
                    next_ai_retry_at = NULL,
                    last_ai_error_code = NULL,
                    last_ai_error_message = NULL,
                    updated_at = now()
                WHERE id = %s
                """,
                (analysis_id, bill_id),
            )

    def mark_ai_skipped(self, bill_id: int, analysis_id: int | None):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET ai_status = 'SUCCESS',
                    last_ai_analysis_id = COALESCE(%s, last_ai_analysis_id),
                    next_ai_retry_at = NULL,
                    last_ai_error_code = NULL,
                    last_ai_error_message = NULL,
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
                SET ai_status = 'RETRY_WAIT',
                    next_ai_retry_at = now() + (%s || ' minutes')::interval,
                    last_ai_error_code = %s,
                    last_ai_error_message = %s,
                    updated_at = now()
                WHERE id = %s
                """,
                (str(wait_minutes), error_code, error_message[:2000], bill_id),
            )

    def mark_ai_permanent_failed(self, bill_id: int, error_code: str, error_message: str):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET ai_status = 'PERMANENT_FAILED',
                    next_ai_retry_at = NULL,
                    last_ai_error_code = %s,
                    last_ai_error_message = %s,
                    updated_at = now()
                WHERE id = %s
                """,
                (error_code, error_message[:2000], bill_id),
            )

    def reset_stale_processing(self, timeout_minutes: int) -> int:
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bills
                SET ai_status = 'RETRY_WAIT',
                    next_ai_retry_at = now(),
                    last_ai_error_code = 'PROCESSING_TIMEOUT',
                    last_ai_error_message = %s,
                    updated_at = now()
                WHERE ai_status = 'PROCESSING'
                  AND last_ai_attempt_at IS NOT NULL
                  AND last_ai_attempt_at < now() - (%s || ' minutes')::interval
                """,
                (
                    f"AI 처리 중 타임아웃이 발생했습니다. {timeout_minutes}분 이상 PROCESSING 상태로 유지되었습니다",
                    str(timeout_minutes),
                ),
            )
            return cur.rowcount

    def get_vote_target_bills(
        self,
        limit: int,
        min_proposal_date: date,
        lookback_days: int,
        missing_only: bool,
    ) -> List[Dict[str, Any]]:
        missing_only_sql = ""
        if missing_only:
            missing_only_sql = f"""
              AND NOT EXISTS (
                    SELECT 1
                    FROM {self.schema}.bill_votes v
                    WHERE v.bill_id = b.id
              )
            """

        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT
                    b.id,
                    b.external_bill_id,
                    b.bill_no,
                    b.official_title,
                    b.proposal_date,
                    b.current_proc_stage_name,
                    EXISTS (
                        SELECT 1
                        FROM {self.schema}.bill_votes v
                        WHERE v.bill_id = b.id
                    ) AS has_votes
                FROM {self.schema}.bills b
                WHERE b.proposal_date IS NOT NULL
                  AND b.proposal_date >= %s
                  AND b.proposal_date >= current_date - (%s || ' days')::interval
                  {missing_only_sql}
                ORDER BY b.proposal_date DESC, b.id DESC
                LIMIT %s
                """,
                (min_proposal_date, str(lookback_days), limit),
            )
            return cur.fetchall()