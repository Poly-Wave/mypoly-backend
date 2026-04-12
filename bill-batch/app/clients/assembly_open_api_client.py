from datetime import datetime, timedelta, timezone
from typing import Any, Dict, Iterator, List
from xml.etree import ElementTree as ET

import requests

from app.config import Settings

_KST = timezone(timedelta(hours=9))


def parse_api_date(value: str | None):
    if not value:
        return None

    value = value.strip()
    if not value:
        return None

    for fmt in ("%Y%m%d", "%Y-%m-%d"):
        try:
            return datetime.strptime(value, fmt).date()
        except ValueError:
            pass

    return None


def parse_api_datetime(value: str | None):
    if not value:
        return None

    value = value.strip()
    if not value:
        return None

    for fmt in ("%Y%m%d%H%M%S", "%Y-%m-%d %H:%M:%S", "%Y-%m-%d", "%Y%m%d"):
        try:
            dt = datetime.strptime(value, fmt)
            return dt.replace(tzinfo=_KST)
        except ValueError:
            pass

    return None


def item_to_dict(item) -> Dict[str, Any]:
    data = {}
    for child in item:
        data[child.tag] = child.text.strip() if child.text else ""
    return data


class AssemblyOpenApiClient:
    MEMBER_INFO_API = "https://open.assembly.go.kr/portal/openapi/ALLNAMEMBER"
    VOTE_INFO_API = "https://open.assembly.go.kr/portal/openapi/nojepdqqaweusdfbi"

    def __init__(self, settings: Settings):
        self.settings = settings
        self.service_key = settings.assembly_service_key.strip()

    def _request_xml(self, url: str, params: Dict[str, Any]) -> ET.Element:
        response = requests.get(
            url,
            params=params,
            timeout=self.settings.bill_batch_request_timeout_sec,
        )
        response.raise_for_status()

        try:
            return ET.fromstring(response.text)
        except ET.ParseError as exc:
            raise RuntimeError(
                f"국회 OpenAPI 응답 XML 파싱 실패 status={response.status_code} body_head={response.text[:500]}"
            ) from exc

    def iter_members(self, age: int) -> Iterator[Dict[str, Any]]:
        page = 1

        while page <= self.settings.bill_batch_member_sync_max_pages:
            params = {
                "KEY": self.service_key,
                "Type": "xml",
                "pIndex": page,
                "pSize": self.settings.bill_batch_member_sync_page_size,
                "AGE": str(age),
            }

            print(f"[ASSEMBLY][MEMBER] 요청 시작 page={page} age={age}", flush=True)

            root = self._request_xml(self.MEMBER_INFO_API, params=params)
            items = root.findall(".//row")

            print(f"[ASSEMBLY][MEMBER] 응답 완료 page={page} item_count={len(items)}", flush=True)

            if not items:
                break

            for item in items:
                raw = item_to_dict(item)

                external_member_id = (item.findtext("NAAS_CD", "") or "").strip()
                if not external_member_id:
                    continue

                era = (item.findtext("GTELT_ERACO", "") or "").strip()
                if era and f"{age}대" not in era:
                    continue

                member = {
                    "external_member_id": external_member_id,
                    "mona_cd": (item.findtext("MONA_CD", "") or "").strip() or None,
                    "member_no": (item.findtext("MEMBER_NO", "") or "").strip() or None,
                    "name": (item.findtext("NAAS_NM", "") or "").strip(),
                    "name_chinese": (item.findtext("NAAS_CH_NM", "") or "").strip() or None,
                    "name_english": (item.findtext("NAAS_EN_NM", "") or "").strip() or None,
                    "party_name": (item.findtext("PLPT_NM", "") or "").strip() or None,
                    "district_name": (item.findtext("ELECD_NM", "") or "").strip() or None,
                    "district_type": (item.findtext("ELECD_DIV_NM", "") or "").strip() or None,
                    "committee_name": (item.findtext("BLNG_CMIT_NM", "") or "").strip() or None,
                    "current_committee_name": (item.findtext("CMIT_NM", "") or "").strip() or None,
                    "era": era or None,
                    "election_type": (item.findtext("RLCT_DIV_NM", "") or "").strip() or None,
                    "gender": (item.findtext("NTR_DIV", "") or "").strip() or None,
                    "birth_date": parse_api_date(item.findtext("BIRDY_DT", "")),
                    "photo_url": (item.findtext("NAAS_PIC", "") or "").strip() or None,
                    "homepage_url": (item.findtext("NAAS_HP_URL", "") or "").strip() or None,
                    "brief_history": (item.findtext("BRF_HST", "") or "").strip() or None,
                    "source_payload": raw,
                }

                if not member["name"]:
                    continue

                yield member

            if len(items) < self.settings.bill_batch_member_sync_page_size:
                break

            page += 1

    def fetch_votes_for_bill(self, external_bill_id: str, age: int) -> List[Dict[str, Any]]:
        params = {
            "KEY": self.service_key,
            "Type": "xml",
            "pIndex": 1,
            "pSize": 300,
            "BILL_ID": external_bill_id,
            "AGE": str(age),
        }

        print(f"[ASSEMBLY][VOTE] 요청 시작 external_bill_id={external_bill_id} age={age}", flush=True)

        root = self._request_xml(self.VOTE_INFO_API, params=params)
        items = root.findall(".//row")

        print(f"[ASSEMBLY][VOTE] 응답 완료 external_bill_id={external_bill_id} item_count={len(items)}", flush=True)

        results: List[Dict[str, Any]] = []

        for item in items:
            raw = item_to_dict(item)

            member_name = (
                (item.findtext("HG_NM", "") or item.findtext("NAAS_NM", "") or "").strip()
            )
            member_no = (item.findtext("MEMBER_NO", "") or "").strip() or None
            mona_cd = (item.findtext("MONA_CD", "") or "").strip() or None
            vote_result = (item.findtext("RESULT_VOTE_MOD", "") or "").strip()

            if not member_name and not member_no and not mona_cd:
                continue

            if not vote_result:
                continue

            vote_date = parse_api_datetime(item.findtext("VOTE_DATE", ""))

            results.append(
                {
                    "external_bill_id": external_bill_id,
                    "member_name": member_name or None,
                    "member_no": member_no,
                    "mona_cd": mona_cd,
                    "party_name_snapshot": (item.findtext("POLY_NM", "") or "").strip() or None,
                    "district_name_snapshot": (item.findtext("ORIG_NM", "") or "").strip() or None,
                    "committee_name_snapshot": (item.findtext("CMIT_NM", "") or "").strip() or None,
                    "vote_result": vote_result,
                    "vote_date": vote_date,
                    "source_payload": raw,
                }
            )

        return results