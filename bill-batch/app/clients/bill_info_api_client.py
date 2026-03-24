import re
import time
from datetime import date, datetime
from typing import Any, Dict, Iterator, List
from urllib.parse import urlencode
from xml.etree import ElementTree as ET

import requests

from app.config import Settings


def parse_api_date(value: str | None):
    if not value:
        return None

    value = value.strip()
    if not value:
        return None

    try:
        if len(value) == 8:
            return datetime.strptime(value, "%Y%m%d").date()
        if len(value) == 10:
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
        self.service_key = settings.bill_service_key.strip()

    def _build_url(self, page: int) -> str:
        query = urlencode(
            {
                "ServiceKey": self.service_key,
                "numOfRows": self.settings.bill_batch_page_size,
                "pageNo": page,
                "start_ord": self.settings.bill_start_ord,
                "end_ord": self.settings.bill_end_ord,
            },
            safe="%",
        )
        return f"{self.settings.bill_api_base_url}/getBillInfoList?{query}"

    def _parse_response(self, response: requests.Response) -> ET.Element:
        try:
            return ET.fromstring(response.text)
        except ET.ParseError as exc:
            raise RuntimeError(
                f"Bill API 응답이 XML 형식이 아닙니다. status={response.status_code}, body_head={response.text[:500]}"
            ) from exc

    def _extract_items(self, root: ET.Element) -> List[ET.Element]:
        items_parent = root.find(".//body/items")
        if items_parent is None:
            return []
        return items_parent.findall("item")

    def _check_api_error(self, root: ET.Element, response: requests.Response):
        result_code = (root.findtext(".//header/resultCode", "") or "").strip()
        result_msg = (root.findtext(".//header/resultMsg", "") or "").strip()

        if result_code and result_code not in {"00", "0", "NORMAL SERVICE"}:
            raise RuntimeError(
                f"Bill API 오류가 발생했습니다. status={response.status_code}, resultCode={result_code}, resultMsg={result_msg}, body_head={response.text[:500]}"
            )

    def iter_bills(self) -> Iterator[Dict[str, Any]]:
        page = 1
        min_proposal_date = parse_api_date(self.settings.min_proposal_date)

        while page <= self.settings.bill_batch_max_pages:
            url = self._build_url(page=page)
            masked_url = url.replace(self.service_key, "***SERVICE_KEY***")
            print(
                f"[BILL_API] 요청 시작 page={page} min_proposal_date={min_proposal_date} url={masked_url}",
                flush=True,
            )

            response = requests.get(url, timeout=self.settings.bill_batch_request_timeout_sec)
            print(
                f"[BILL_API] 응답 수신 page={page} status={response.status_code} body_head={response.text[:300]!r}",
                flush=True,
            )
            response.raise_for_status()

            root = self._parse_response(response)
            self._check_api_error(root, response)

            total_count_text = (root.findtext(".//body/totalCount", "") or "").strip()
            total_count = int(total_count_text) if total_count_text.isdigit() else None
            items = self._extract_items(root)

            print(
                f"[BILL_API] 파싱 완료 page={page} total_count={total_count} item_count={len(items)}",
                flush=True,
            )

            if not items:
                print(f"[BILL_API] page={page} 에 조회된 항목이 없어 페이지 순회를 종료합니다", flush=True)
                break

            page_bills: List[Dict[str, Any]] = []
            page_dates: List[date] = []

            for item in items:
                raw = item_to_dict(item)

                external_bill_id = (item.findtext("billId", "") or "").strip()
                if not external_bill_id:
                    continue

                proposal_date = parse_api_date(item.findtext("proposeDt", ""))
                official_title = (item.findtext("billName", "") or "").strip()
                if not official_title:
                    continue

                if proposal_date:
                    page_dates.append(proposal_date)

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

                detail_url = f"https://likms.assembly.go.kr/bill/billDetail.do?billId={external_bill_id}"

                page_bills.append(
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

            if not page_bills:
                print(f"[BILL_API] page={page} 에 유효한 의안 데이터가 없어 페이지 순회를 종료합니다", flush=True)
                break

            if page_dates:
                page_min_date = min(page_dates)
                page_max_date = max(page_dates)
                print(
                    f"[BILL_API] page={page} 제안일 범위={page_min_date}~{page_max_date}",
                    flush=True,
                )

                if min_proposal_date and page_max_date < min_proposal_date:
                    print(
                        f"[BILL_API] page_max_date={page_max_date} 가 min_proposal_date={min_proposal_date} 보다 이전이므로 페이지 순회를 종료합니다",
                        flush=True,
                    )
                    break

            for bill in page_bills:
                yield bill

            page += 1

            if self.settings.bill_batch_sleep_ms > 0:
                time.sleep(self.settings.bill_batch_sleep_ms / 1000)