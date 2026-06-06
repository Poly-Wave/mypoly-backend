"""
국회 OpenAPI - 본회의 처리 의안 (TVBPMBILL11) 클라이언트.

- 기존: data.go.kr 의 BillInfoService2 (deprecated 됨)
- 신규: open.assembly.go.kr 의 TVBPMBILL11
- 인증키: ASSEMBLY_SERVICE_KEY 재사용 (옛 BILL_SERVICE_KEY 폐기)

기존 코드와의 호환을 위해 yield 하는 dict 는 옛 BillInfoService2 응답 구조를 유지한다.
다만 신 API 가 제공하지 않는 필드(summary 등)는 결과 dict 에 포함하지 않아
upsert_bill 단계에서 기존 DB 값이 NULL 로 덮어쓰이지 않게 한다.
"""

import re
import time
from datetime import date, datetime, timedelta, timezone
from typing import Any, Dict, Iterator, List, Optional, Tuple

import requests

from app.config import Settings

_KST = timezone(timedelta(hours=9))


def kst_today() -> date:
    return datetime.now(_KST).date()


def parse_api_date(value: Optional[str]) -> Optional[date]:
    if not value:
        return None

    value = value.strip()
    if not value:
        return None

    for fmt in ("%Y-%m-%d", "%Y%m%d"):
        try:
            return datetime.strptime(value, fmt).date()
        except ValueError:
            continue
    return None


# 옛 BillInfoService2 의 procStageCode 명명과 호환되는 단계명.
# 우리 DB 의 bill_status_history 가 unique key 로 stage_code 를 쓰므로,
# 옛 명명과 일치시켜야 알림 행 6 (북마크 단계 변경) 에서 중복 알림이 발생하지 않는다.
_STAGE_ORDER_RECEIVED = 1
_STAGE_ORDER_COMMITTEE = 2
_STAGE_ORDER_LAW = 2
_STAGE_ORDER_PLENARY = 3
_STAGE_ORDER_PROMULGATED = 4


def extract_representative_proposer_name(title: str, fallback: Optional[str]) -> str:
    """제목에서 '○○○의원' 패턴을 추출. fallback 이 있으면 우선 사용."""
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


def extract_proposer_count(title: str, proposer: Optional[str]) -> int:
    """제목/제안자 문자열에서 '등 N인' 패턴을 추출."""
    sources = [title or "", proposer or ""]
    for source in sources:
        match = re.search(r"등\s*([0-9]+)인", source)
        if match:
            try:
                return int(match.group(1))
            except ValueError:
                continue

    for source in sources:
        if "의원" in source or "제안" in source:
            return 1

    return 0


def _resolve_current_stage(item: Dict[str, Any]) -> Tuple[Optional[str], Optional[str], Optional[int], Optional[date]]:
    """
    신 API 의 단계별 날짜를 보고 현재 진행 단계를 결정한다.

    우선순위 (높은 → 낮음):
      본회의 처리 > 법사위 처리/상정/회부 > 소관위 처리/상정/회부 > 접수

    옛 BillInfoService2 의 procStageCode 명명과 가능한 한 호환되도록 한국어 라벨을 사용한다.
    실제 prod DB 의 옛 distinct stage_code 값에 맞춰 추후 조정 가능.
    """
    proc_dt = parse_api_date(item.get("PROC_DT"))
    law_proc_dt = parse_api_date(item.get("LAW_PROC_DT"))
    law_present_dt = parse_api_date(item.get("LAW_PRESENT_DT"))
    law_submit_dt = parse_api_date(item.get("LAW_SUBMIT_DT"))
    cmt_proc_dt = parse_api_date(item.get("CMT_PROC_DT"))
    cmt_present_dt = parse_api_date(item.get("CMT_PRESENT_DT"))
    committee_dt = parse_api_date(item.get("COMMITTEE_DT"))

    if proc_dt:
        return ("BILL_PASS_GUBN_PLENARY", "본회의 의결", _STAGE_ORDER_PLENARY, proc_dt)
    if law_proc_dt:
        return ("BILL_PASS_GUBN_LAW_PROC", "법사위 처리", _STAGE_ORDER_LAW, law_proc_dt)
    if law_present_dt:
        return ("BILL_PASS_GUBN_LAW_PRESENT", "법사위 상정", _STAGE_ORDER_LAW, law_present_dt)
    if law_submit_dt:
        return ("BILL_PASS_GUBN_LAW_SUBMIT", "법사위 회부", _STAGE_ORDER_LAW, law_submit_dt)
    if cmt_proc_dt:
        return ("BILL_PASS_GUBN_CMT_PROC", "소관위 처리", _STAGE_ORDER_COMMITTEE, cmt_proc_dt)
    if cmt_present_dt:
        return ("BILL_PASS_GUBN_CMT_PRESENT", "소관위 상정", _STAGE_ORDER_COMMITTEE, cmt_present_dt)
    if committee_dt:
        return ("BILL_PASS_GUBN_CMT_SUBMIT", "소관위 회부", _STAGE_ORDER_COMMITTEE, committee_dt)
    return ("BILL_RCP", "접수", _STAGE_ORDER_RECEIVED, None)


def _build_status_history_entries(item: Dict[str, Any], pass_gubn: Optional[str], proc_result: Optional[str]) -> List[Dict[str, Any]]:
    """
    이력 multi-insert 용 단계별 entry list.

    각 단계의 날짜가 채워져 있으면 history row 후보. dedup unique index 가 멱등성을 보장한다.
    """
    entries: List[Dict[str, Any]] = []

    def add(stage_code: str, stage_name: str, stage_order: int, proc_date: Optional[date], result: Optional[str] = None):
        if proc_date is None:
            return
        entries.append({
            "stage_code": stage_code,
            "stage_name": stage_name,
            "stage_order": stage_order,
            "pass_gubn": pass_gubn,
            "general_result": result,
            "proc_date": proc_date,
        })

    # 소관위 단계
    add("BILL_PASS_GUBN_CMT_SUBMIT", "소관위 회부", _STAGE_ORDER_COMMITTEE, parse_api_date(item.get("COMMITTEE_DT")))
    add("BILL_PASS_GUBN_CMT_PRESENT", "소관위 상정", _STAGE_ORDER_COMMITTEE, parse_api_date(item.get("CMT_PRESENT_DT")))
    add("BILL_PASS_GUBN_CMT_PROC", "소관위 처리", _STAGE_ORDER_COMMITTEE, parse_api_date(item.get("CMT_PROC_DT")), item.get("CMT_PROC_RESULT_CD"))

    # 법사위 단계
    add("BILL_PASS_GUBN_LAW_SUBMIT", "법사위 회부", _STAGE_ORDER_LAW, parse_api_date(item.get("LAW_SUBMIT_DT")))
    add("BILL_PASS_GUBN_LAW_PRESENT", "법사위 상정", _STAGE_ORDER_LAW, parse_api_date(item.get("LAW_PRESENT_DT")))
    add("BILL_PASS_GUBN_LAW_PROC", "법사위 처리", _STAGE_ORDER_LAW, parse_api_date(item.get("LAW_PROC_DT")), item.get("LAW_PROC_RESULT_CD"))

    # 본회의 처리
    add("BILL_PASS_GUBN_PLENARY", "본회의 의결", _STAGE_ORDER_PLENARY, parse_api_date(item.get("PROC_DT")), proc_result)

    return entries


class BillInfoApiClient:
    """신 API (open.assembly.go.kr / TVBPMBILL11) 호출 클라이언트."""

    ENDPOINT = "TVBPMBILL11"

    def __init__(self, settings: Settings):
        self.settings = settings
        # 신 API 는 open.assembly.go.kr 라 ASSEMBLY_SERVICE_KEY 를 사용한다.
        # (옛 BillInfoService2 의 BILL_SERVICE_KEY 는 폐기됨.)
        self.service_key = (settings.assembly_service_key or "").strip()
        if not self.service_key:
            raise ValueError("의안 API 인증키가 없습니다. ASSEMBLY_SERVICE_KEY 를 설정해주세요.")

        # base URL: 신 API 호스트.
        base = (settings.bill_api_base_url or "").strip().rstrip("/")
        self.base_url = base or "https://open.assembly.go.kr/portal/openapi"

    def _build_url(self) -> str:
        return f"{self.base_url}/{self.ENDPOINT}"

    # 단발성 네트워크 오류 / 5xx 에 대한 retry 횟수. 4xx 는 retry 해도 의미 없으므로 즉시 raise.
    _MAX_RETRIES = 2
    # 지수 백오프 기준 단위 (초). 시도 N 회차에서 _BACKOFF_BASE_SEC * 2**N 만큼 대기.
    _BACKOFF_BASE_SEC = 1.0

    def _request_json(self, page: int) -> Dict[str, Any]:
        params = {
            "KEY": self.service_key,
            "Type": "json",
            "pIndex": page,
            "pSize": self.settings.bill_batch_page_size,
            "AGE": str(self.settings.bill_start_ord),
        }

        masked = {**params, "KEY": "***SERVICE_KEY***"}
        print(
            f"[BILL_API] 요청 시작 page={page} url={self._build_url()} params={masked}",
            flush=True,
        )

        last_exc: Optional[Exception] = None
        for attempt in range(self._MAX_RETRIES + 1):
            try:
                response = requests.get(
                    self._build_url(),
                    params=params,
                    timeout=self.settings.bill_batch_request_timeout_sec,
                )
                print(
                    f"[BILL_API] 응답 수신 page={page} status={response.status_code} attempt={attempt + 1}",
                    flush=True,
                )

                # 4xx 는 키/파라미터 문제라 retry 해도 동일하게 실패한다 → 즉시 raise.
                if 400 <= response.status_code < 500:
                    response.raise_for_status()

                # 5xx 또는 네트워크 에러는 retry 대상.
                response.raise_for_status()

                try:
                    return response.json()
                except ValueError as exc:
                    raise RuntimeError(
                        f"Bill API 응답이 JSON 이 아닙니다. status={response.status_code} body_head={response.text[:500]!r}"
                    ) from exc

            except requests.HTTPError as exc:
                last_exc = exc
                status = exc.response.status_code if exc.response is not None else None
                # 4xx 는 재시도 가치 없음
                if status is not None and 400 <= status < 500:
                    raise
                # 5xx 는 retry
                if attempt < self._MAX_RETRIES:
                    wait_sec = self._BACKOFF_BASE_SEC * (2 ** attempt)
                    print(
                        f"[BILL_API][retry] page={page} attempt={attempt + 1} status={status} wait={wait_sec}s",
                        flush=True,
                    )
                    time.sleep(wait_sec)
                    continue
                raise

            except (requests.ConnectionError, requests.Timeout) as exc:
                last_exc = exc
                if attempt < self._MAX_RETRIES:
                    wait_sec = self._BACKOFF_BASE_SEC * (2 ** attempt)
                    print(
                        f"[BILL_API][retry] page={page} attempt={attempt + 1} error={type(exc).__name__} wait={wait_sec}s",
                        flush=True,
                    )
                    time.sleep(wait_sec)
                    continue
                raise

        # 이론상 도달 불가 — 위에서 return 또는 raise.
        if last_exc:
            raise last_exc
        raise RuntimeError(f"Bill API 요청 실패 (이유 불명) page={page}")

    def _parse_envelope(self, payload: Dict[str, Any]) -> Tuple[Optional[int], str, str, List[Dict[str, Any]]]:
        """
        TVBPMBILL11 응답 구조:
          { "TVBPMBILL11": [
              { "head": [ {"list_total_count": N}, {"RESULT": {"CODE": "INFO-000", ...}} ] },
              { "row": [ {...}, {...} ] }
          ] }

        루트가 RESULT 단독이면 에러 응답.
        """
        # 루트 RESULT 단독 = 에러
        if "RESULT" in payload and isinstance(payload["RESULT"], dict):
            result = payload["RESULT"]
            return None, str(result.get("CODE", "")), str(result.get("MESSAGE", "")), []

        envelope = payload.get(self.ENDPOINT)
        if not isinstance(envelope, list):
            return None, "PARSE_ERROR", f"엔벨로프 키 {self.ENDPOINT} 가 응답에 없음", []

        total_count: Optional[int] = None
        code = ""
        message = ""
        rows: List[Dict[str, Any]] = []

        for block in envelope:
            if not isinstance(block, dict):
                continue
            for head in block.get("head", []) or []:
                if not isinstance(head, dict):
                    continue
                if "list_total_count" in head:
                    try:
                        total_count = int(head["list_total_count"])
                    except (TypeError, ValueError):
                        total_count = None
                if "RESULT" in head and isinstance(head["RESULT"], dict):
                    code = str(head["RESULT"].get("CODE", ""))
                    message = str(head["RESULT"].get("MESSAGE", ""))
            row = block.get("row")
            if isinstance(row, list):
                rows.extend([r for r in row if isinstance(r, dict)])

        return total_count, code, message, rows

    def _convert_row(self, item: Dict[str, Any]) -> Optional[Dict[str, Any]]:
        """
        신 API row → 기존 batch 가 기대하는 bill dict 로 변환.
        - summary_raw 는 신 API 에 없음. 결과 dict 에 키 자체를 넣지 않아 upsert_bill 단계에서
          기존 DB 값이 보존되도록 한다. (D1 결정사항)
        """
        external_bill_id = (item.get("BILL_ID") or "").strip()
        if not external_bill_id:
            return None

        official_title = (item.get("BILL_NAME") or "").strip()
        if not official_title:
            return None

        proposal_date = parse_api_date(item.get("PROPOSE_DT"))
        bill_no = (item.get("BILL_NO") or "").strip() or None
        proposer_kind = (item.get("PROPOSER_KIND") or "").strip() or None
        proposer = (item.get("PROPOSER") or "").strip() or None
        rst_proposer = (item.get("RST_PROPOSER") or "").strip() or None

        representative_proposer_name = extract_representative_proposer_name(
            official_title,
            rst_proposer or proposer,
        )
        proposer_count = extract_proposer_count(official_title, proposer)

        # 현재 진행 단계
        stage_code, stage_name, stage_order, status_proc_date = _resolve_current_stage(item)

        pass_gubn = (item.get("PASS_GUBUN") or "").strip() or None
        proc_result = (item.get("PROC_RESULT_CD") or "").strip() or None

        detail_url = (item.get("LINK_URL") or "").strip()
        if not detail_url:
            detail_url = f"https://likms.assembly.go.kr/bill/billDetail.do?billId={external_bill_id}"

        status_history_entries = _build_status_history_entries(item, pass_gubn, proc_result)

        result: Dict[str, Any] = {
            "external_bill_id": external_bill_id,
            "bill_no": bill_no,
            "official_title": official_title,
            "proposal_date": proposal_date,
            "proposer_kind": proposer_kind,
            "representative_proposer_name": representative_proposer_name,
            "proposer_count": proposer_count,
            # NOTE: summary_raw 는 의도적으로 누락. upsert_bill 이 dict.get("summary_raw") → None 으로 받고
            # repository 단계에서 "summary_raw 가 dict 에 없으면 기존 값 유지" 정책으로 보존된다.
            "detail_url": detail_url,
            "current_proc_stage_code": stage_code,
            "current_proc_stage_name": stage_name,
            "current_proc_stage_order": stage_order,
            "current_pass_gubn": pass_gubn,
            "current_general_result": proc_result,
            "status_proc_date": status_proc_date,
            "source_payload": item,
            "_status_history_entries": status_history_entries,
        }
        return result

    def iter_bills(self) -> Iterator[Dict[str, Any]]:
        """
        TVBPMBILL11 페이지네이션 순회.

        - AGE=22 로 22대 의안만 fetch
        - 응답이 PROPOSE_DT 내림차순으로 와서 page_max_date 가 min_proposal_date 보다 이전이면 break
          (옛 BillInfoService2 의 종료 조건 동일)
        """
        page = 1
        min_proposal_date = parse_api_date(self.settings.min_proposal_date)
        max_pages = self.settings.bill_batch_max_pages

        while page <= max_pages:
            try:
                payload = self._request_json(page=page)
            except Exception as exc:
                print(f"[BILL_API][오류] page={page} request failed: {exc}", flush=True)
                raise

            total_count, code, message, rows = self._parse_envelope(payload)

            print(
                f"[BILL_API] 파싱 완료 page={page} total_count={total_count} item_count={len(rows)} code={code}",
                flush=True,
            )

            # 신 API 의 정상/없음/에러 처리
            if code == "INFO-200":
                print(f"[BILL_API] page={page} 데이터 없음. 페이지 순회를 종료합니다", flush=True)
                break
            if code and not code.startswith("INFO"):
                raise RuntimeError(
                    f"Bill API 오류가 발생했습니다. page={page} code={code} message={message}"
                )

            if not rows:
                print(f"[BILL_API] page={page} rows 비어 있음. 페이지 순회를 종료합니다", flush=True)
                break

            converted: List[Dict[str, Any]] = []
            page_dates: List[date] = []

            for raw in rows:
                # AGE 가 22 가 아닌 row (혹시 들어오면) skip
                age_value = (raw.get("AGE") or "").strip()
                if age_value and age_value != str(self.settings.bill_start_ord):
                    continue

                bill = self._convert_row(raw)
                if bill is None:
                    continue

                if bill.get("proposal_date"):
                    page_dates.append(bill["proposal_date"])

                converted.append(bill)

            if not converted:
                print(f"[BILL_API] page={page} 유효 row 0. 페이지 순회를 종료합니다", flush=True)
                break

            if page_dates:
                page_min_date = min(page_dates)
                page_max_date = max(page_dates)
                print(
                    f"[BILL_API] page={page} 제안일 범위={page_min_date}~{page_max_date}",
                    flush=True,
                )
                # 정렬이 내림차순이라 page_max_date (이 페이지의 가장 최신) 가 cutoff 보다 이전이면
                # 이후 페이지는 더 옛 의안만 — break.
                if min_proposal_date and page_max_date < min_proposal_date:
                    print(
                        f"[BILL_API] page_max_date={page_max_date} 가 min_proposal_date={min_proposal_date} 보다 이전이므로 페이지 순회를 종료합니다",
                        flush=True,
                    )
                    break

            for bill in converted:
                yield bill

            # 페이지 한도 (total_count 가 알려진 경우 도달 시 break)
            if total_count is not None and page * self.settings.bill_batch_page_size >= total_count:
                print(f"[BILL_API] 총 건수 도달 total_count={total_count} page={page}. 종료", flush=True)
                break

            page += 1

            if self.settings.bill_batch_sleep_ms > 0:
                time.sleep(self.settings.bill_batch_sleep_ms / 1000)
