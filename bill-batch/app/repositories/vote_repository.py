from datetime import datetime
from typing import Any, Dict, Tuple

from psycopg2.extras import Json


class VoteRepository:
    def __init__(self, conn, schema: str):
        self.conn = conn
        self.schema = schema

    def _find_by_external_vote_key(self, external_vote_key: str):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT *
                FROM {self.schema}.bill_votes
                WHERE external_vote_key = %s
                """,
                (external_vote_key,),
            )
            return cur.fetchone()

    def build_external_vote_key(
        self,
        external_bill_id: str,
        member_no: str | None,
        mona_cd: str | None,
        member_name: str | None,
        vote_date: datetime | None,
        vote_result: str,
    ) -> str:
        vote_date_text = vote_date.isoformat() if vote_date else "NO_DATE"
        member_key = (member_no or mona_cd or member_name or "UNKNOWN").strip()
        return f"{external_bill_id}:{member_key}:{vote_date_text}:{vote_result.strip()}"

    def upsert_vote(self, vote: Dict[str, Any]) -> Tuple[int, str]:
        existing = self._find_by_external_vote_key(vote["external_vote_key"])

        comparable = {
            "bill_id": vote.get("bill_id"),
            "member_id": vote.get("member_id"),
            "member_name": vote.get("member_name"),
            "member_no": vote.get("member_no"),
            "mona_cd": vote.get("mona_cd"),
            "party_name_snapshot": vote.get("party_name_snapshot"),
            "district_name_snapshot": vote.get("district_name_snapshot"),
            "committee_name_snapshot": vote.get("committee_name_snapshot"),
            "vote_result": vote.get("vote_result"),
            "vote_date": vote.get("vote_date"),
        }

        if not existing:
            with self.conn.cursor() as cur:
                cur.execute(
                    f"""
                    INSERT INTO {self.schema}.bill_votes (
                        bill_id,
                        member_id,
                        external_vote_key,
                        member_name,
                        member_no,
                        mona_cd,
                        party_name_snapshot,
                        district_name_snapshot,
                        committee_name_snapshot,
                        vote_result,
                        vote_date,
                        source_payload,
                        created_at
                    ) VALUES (
                        %s, %s, %s,
                        %s, %s, %s,
                        %s, %s, %s,
                        %s, %s,
                        %s,
                        now()
                    )
                    RETURNING id
                    """,
                    (
                        vote["bill_id"],
                        vote.get("member_id"),
                        vote["external_vote_key"],
                        vote.get("member_name"),
                        vote.get("member_no"),
                        vote.get("mona_cd"),
                        vote.get("party_name_snapshot"),
                        vote.get("district_name_snapshot"),
                        vote.get("committee_name_snapshot"),
                        vote["vote_result"],
                        vote.get("vote_date"),
                        Json(vote.get("source_payload", {})),
                    ),
                )
                return cur.fetchone()["id"], "INSERT"

        no_change = all(existing.get(key) == value for key, value in comparable.items())
        if no_change:
            return existing["id"], "NO_CHANGE"

        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bill_votes
                SET
                    bill_id = %s,
                    member_id = %s,
                    member_name = %s,
                    member_no = %s,
                    mona_cd = %s,
                    party_name_snapshot = %s,
                    district_name_snapshot = %s,
                    committee_name_snapshot = %s,
                    vote_result = %s,
                    vote_date = %s,
                    source_payload = %s
                WHERE id = %s
                """,
                (
                    vote["bill_id"],
                    vote.get("member_id"),
                    vote.get("member_name"),
                    vote.get("member_no"),
                    vote.get("mona_cd"),
                    vote.get("party_name_snapshot"),
                    vote.get("district_name_snapshot"),
                    vote.get("committee_name_snapshot"),
                    vote["vote_result"],
                    vote.get("vote_date"),
                    Json(vote.get("source_payload", {})),
                    existing["id"],
                ),
            )
        return existing["id"], "UPDATE"