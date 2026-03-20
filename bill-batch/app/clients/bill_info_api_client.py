import re
import time
from datetime import date
from typing import Any, Dict, List
from urllib.parse import unquote
from xml.etree import ElementTree as ET

import requests

from app.config import Settings


BILL_INFO_API = "https://apis.data.go.kr/9710000/BillInfoService2/getBillInfoList"


def parse_api_date(value: str | None):
    if not value:
        return None
    value = value.strip()
    if not value:
        return None

    try:
        if len(value) == 8:
            from datetime import datetime
            return datetime.strptime(value, "%Y%m%d").date()
        if len(value) == 10:
            from datetime import datetime
            return datetime.strptime(value, "%Y-%m-%d").date()
    except Exception:
        return None
    return None


def calculate_proc_stage_order(proc_stage_value: str | None):
    if not proc_stage_value:
        return None

    value = proc_stage_value.strip()
    if not value:
        return None

    if "접수" in value:
        return 1
    if "심사" in value:
        return 2
    if "본회의" in value:
        return 3
    if any(keyword in value for keyword in ["공포", "정부이송", "처리완료"]):
        return 4
    return None


def extract_representative_proposer_name(title: str, fallback: str | None):
    if fallback and fallback.strip():
        return fallback.strip()

    match = re.search(r"\(([^)]+)\)", title or "")
    if not match:
        return ""

    proposer_text = match.group(1)
    proposer_match = re.search(r"([가-힣]+)의원", proposer_text)
    if proposer_match:
        return proposer_match.group(1)
    return proposer_text.strip()


def extract_proposer_count(title: str) -> int:
    if not title:
        return 0

    match = re.search(r"등\s*([0-9]+)인", title)
    if match:
        try:
            return int(match.group(1))
        except ValueError:
            return 0

    if "의원" in title or "제안" in title:
        return 1
    return 0


def item_to_dict(item) -> Dict[str, Any]:
    data = {}
    for child in item:
        data[child.tag] = child.text.strip() if child.text else ""
    return data


class BillInfoApiClient:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.decoded_service_key = unquote(settings.bill_service_key)

    def fetch_bills(self, start_date: date, end_date: date) -> List[Dict[str, Any]]:
        page = 1
        results: List[Dict[str, Any]] = []

        while True:
            params = {
                "serviceKey": self.decoded_service_key,
                "numOfRows": self.settings.bill_batch_page_size,
                "pageNo": page,
                "proposeDtFrom": start_date.strftime("%Y%m%d"),
                "proposeDtTo": end_date.strftime("%Y%m%d"),
            }

            response = requests.get(BILL_INFO_API, params=params, timeout=30)
            response.raise_for_status()

            root = ET.fromstring(response.text)
            items = root.findall(".//items/item")
            if not items:
                break

            for item in items:
                raw = item_to_dict(item)

                external_bill_id = (item.findtext("billId", "") or "").strip()
                if not external_bill_id:
                    continue

                proposal_date = parse_api_date(item.findtext("proposeDt", ""))
                official_title = (item.findtext("billName", "") or "").strip()
                if not official_title:
                    continue

                bill_no = (item.findtext("billNo", "") or "").strip()
                proposer_kind = (item.findtext("proposerKind", "") or "").strip()

                proposer_name_api = (
                    item.findtext("proposerNm", "")
                    or item.findtext("proposerName", "")
                    or ""
                ).strip()

                representative_proposer_name = extract_representative_proposer_name(
                    official_title,
                    proposer_name_api,
                )
                proposer_count = extract_proposer_count(official_title)

                proc_stage_code = (
                    item.findtext("procStageCd", "")
                    or item.findtext("procStageCode", "")
                    or ""
                ).strip()

                proc_stage_name = (
                    item.findtext("procStage", "")
                    or item.findtext("procStageNm", "")
                    or item.findtext("procStageName", "")
                    or proc_stage_code
                ).strip()

                pass_gubn = (item.findtext("passGubn", "") or "").strip()
                proc_date = parse_api_date(item.findtext("procDt", ""))
                general_result = (item.findtext("generalResult", "") or "").strip()
                summary_raw = (item.findtext("summary", "") or "").strip()

                detail_url = (
                    f"https://likms.assembly.go.kr/bill/billDetail.do?billId={external_bill_id}"
                )

                results.append(
                    {
                        "external_bill_id": external_bill_id,
                        "bill_no": bill_no,
                        "official_title": official_title,
                        "proposal_date": proposal_date,
                        "proposer_kind": proposer_kind,
                        "representative_proposer_name": representative_proposer_name,
                        "proposer_count": proposer_count,
                        "summary_raw": summary_raw,
                        "detail_url": detail_url,
                        "current_proc_stage_code": proc_stage_code,
                        "current_proc_stage_name": proc_stage_name,
                        "current_proc_stage_order": calculate_proc_stage_order(proc_stage_name or proc_stage_code),
                        "current_pass_gubn": pass_gubn,
                        "current_general_result": general_result,
                        "status_proc_date": proc_date,
                        "source_payload": raw,
                    }
                )

            page += 1
            time.sleep(self.settings.bill_batch_sleep_ms / 1000.0)

        return results