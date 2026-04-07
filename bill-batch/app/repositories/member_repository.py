from typing import Any, Dict, Tuple

from psycopg2.extras import Json


class MemberRepository:
    def __init__(self, conn, schema: str):
        self.conn = conn
        self.schema = schema

    def _find_by_external_member_id(self, external_member_id: str):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT *
                FROM {self.schema}.bill_members
                WHERE external_member_id = %s
                """,
                (external_member_id,),
            )
            return cur.fetchone()

    def upsert_member(self, member: Dict[str, Any]) -> Tuple[int, str]:
        existing = self._find_by_external_member_id(member["external_member_id"])

        comparable = {
            "mona_cd": member.get("mona_cd"),
            "member_no": member.get("member_no"),
            "name": member.get("name"),
            "name_chinese": member.get("name_chinese"),
            "name_english": member.get("name_english"),
            "party_name": member.get("party_name"),
            "district_name": member.get("district_name"),
            "district_type": member.get("district_type"),
            "committee_name": member.get("committee_name"),
            "current_committee_name": member.get("current_committee_name"),
            "era": member.get("era"),
            "election_type": member.get("election_type"),
            "gender": member.get("gender"),
            "birth_date": member.get("birth_date"),
            "photo_url": member.get("photo_url"),
            "homepage_url": member.get("homepage_url"),
            "brief_history": member.get("brief_history"),
        }

        if not existing:
            with self.conn.cursor() as cur:
                cur.execute(
                    f"""
                    INSERT INTO {self.schema}.bill_members (
                        external_member_id,
                        mona_cd,
                        member_no,
                        name,
                        name_chinese,
                        name_english,
                        party_name,
                        district_name,
                        district_type,
                        committee_name,
                        current_committee_name,
                        era,
                        election_type,
                        gender,
                        birth_date,
                        photo_url,
                        homepage_url,
                        brief_history,
                        source_payload,
                        created_at,
                        updated_at
                    ) VALUES (
                        %s, %s, %s, %s, %s, %s,
                        %s, %s, %s,
                        %s, %s,
                        %s, %s, %s, %s,
                        %s, %s, %s,
                        %s,
                        now(), now()
                    )
                    RETURNING id
                    """,
                    (
                        member["external_member_id"],
                        member.get("mona_cd"),
                        member.get("member_no"),
                        member["name"],
                        member.get("name_chinese"),
                        member.get("name_english"),
                        member.get("party_name"),
                        member.get("district_name"),
                        member.get("district_type"),
                        member.get("committee_name"),
                        member.get("current_committee_name"),
                        member.get("era"),
                        member.get("election_type"),
                        member.get("gender"),
                        member.get("birth_date"),
                        member.get("photo_url"),
                        member.get("homepage_url"),
                        member.get("brief_history"),
                        Json(member.get("source_payload", {})),
                    ),
                )
                return cur.fetchone()["id"], "INSERT"

        no_change = all(existing.get(key) == value for key, value in comparable.items())
        if no_change:
            return existing["id"], "NO_CHANGE"

        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bill_members
                SET
                    mona_cd = %s,
                    member_no = %s,
                    name = %s,
                    name_chinese = %s,
                    name_english = %s,
                    party_name = %s,
                    district_name = %s,
                    district_type = %s,
                    committee_name = %s,
                    current_committee_name = %s,
                    era = %s,
                    election_type = %s,
                    gender = %s,
                    birth_date = %s,
                    photo_url = %s,
                    homepage_url = %s,
                    brief_history = %s,
                    source_payload = %s,
                    updated_at = now()
                WHERE id = %s
                """,
                (
                    member.get("mona_cd"),
                    member.get("member_no"),
                    member["name"],
                    member.get("name_chinese"),
                    member.get("name_english"),
                    member.get("party_name"),
                    member.get("district_name"),
                    member.get("district_type"),
                    member.get("committee_name"),
                    member.get("current_committee_name"),
                    member.get("era"),
                    member.get("election_type"),
                    member.get("gender"),
                    member.get("birth_date"),
                    member.get("photo_url"),
                    member.get("homepage_url"),
                    member.get("brief_history"),
                    Json(member.get("source_payload", {})),
                    existing["id"],
                ),
            )
        return existing["id"], "UPDATE"

    def build_lookup_maps(self) -> Dict[str, Dict[str, int]]:
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT id, member_no, mona_cd, name, party_name
                FROM {self.schema}.bill_members
                """
            )
            rows = cur.fetchall()

        by_member_no: Dict[str, int] = {}
        by_mona_cd: Dict[str, int] = {}
        by_name_party: Dict[str, int] = {}

        for row in rows:
            member_id = row["id"]

            member_no = (row.get("member_no") or "").strip()
            if member_no and member_no not in by_member_no:
                by_member_no[member_no] = member_id

            mona_cd = (row.get("mona_cd") or "").strip()
            if mona_cd and mona_cd not in by_mona_cd:
                by_mona_cd[mona_cd] = member_id

            name = (row.get("name") or "").strip()
            party_name = (row.get("party_name") or "").strip()
            if name:
                key = f"{name}|{party_name}"
                if key not in by_name_party:
                    by_name_party[key] = member_id

        return {
            "by_member_no": by_member_no,
            "by_mona_cd": by_mona_cd,
            "by_name_party": by_name_party,
        }

    def resolve_member_id(
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

        if member_name:
            key = f"{member_name}|{party_name}"
            return lookup_maps["by_name_party"].get(key)

        return None