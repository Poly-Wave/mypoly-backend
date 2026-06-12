"""
신 API (TVBPMBILL11) 마이그레이션 검증용 dry-run 스크립트.

- DB 에 쓰지 않고 client 응답만 파싱해서 stdout 으로 확인한다.
- 검증 포인트:
  1) TVBPMBILL11 응답이 정상 파싱되는지
  2) AGE=22 필터로 22대 의안만 들어오는지
  3) min_proposal_date 필터로 2026-01-01 이전 의안에서 break 되는지
  4) 단계 계산 (current_proc_stage_*) 이 정상 동작하는지
  5) bill_status_history multi-insert 용 entries 가 잘 생성되는지

NOTE: 이 파일은 production image 에 포함되지 않는다.
      Dockerfile 이 `COPY app ./app` 만 수행하므로 루트의 dry_run.py 는 컨테이너에 들어가지 않는다.
      향후 신 API 응답 구조가 바뀌거나, 마이그레이션 회귀 검증이 필요할 때 로컬에서 실행한다.

실행:
  cd bill-batch
  ASSEMBLY_SERVICE_KEY=<key> python dry_run.py
"""

import os
import sys

# bill-batch 디렉토리를 PYTHONPATH 에 추가 (app 모듈 import 위해)
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from app.clients.bill_info_api_client import BillInfoApiClient
from app.config import Settings


def _build_dry_run_settings() -> Settings:
    """DB 등 필수가 아닌 값은 dummy 로 채워서 settings 객체를 만든다."""
    key = (os.getenv("ASSEMBLY_SERVICE_KEY") or "").strip()
    if not key:
        raise SystemExit("ASSEMBLY_SERVICE_KEY 환경변수가 필요합니다.")

    return Settings(
        db_host="dummy",
        db_port=5432,
        db_name="dummy",
        db_username="dummy",
        db_password="dummy",
        db_schema="bill_service",

        bill_api_base_url=os.getenv("BILL_API_BASE_URL", "https://open.assembly.go.kr/portal/openapi"),
        bill_start_ord=int(os.getenv("BILL_START_ORD", "22")),
        bill_end_ord=int(os.getenv("BILL_END_ORD", "22")),

        assembly_service_key=key,
        bill_batch_member_age=22,
        bill_batch_member_sync_page_size=300,
        bill_batch_member_sync_max_pages=10,

        gemini_keys=[],
        gemini_model="gemini-2.5-flash",
        gemini_temperature=0.2,

        bill_batch_job_type="DRY_RUN",
        bill_batch_trigger_type="MANUAL",
        bill_batch_page_size=int(os.getenv("BILL_BATCH_PAGE_SIZE", "10")),  # dry-run 은 작게
        bill_batch_max_pages=int(os.getenv("BILL_BATCH_MAX_PAGES", "3")),    # 3페이지만
        bill_batch_request_timeout_sec=30,
        bill_batch_sleep_ms=150,
        bill_batch_ai_sleep_ms=0,

        bill_batch_enable_bill_collect=True,
        bill_batch_enable_member_sync=False,
        bill_batch_enable_vote_sync=False,
        bill_batch_enable_ai=False,

        bill_batch_vote_sync_missing_only=True,
        bill_batch_vote_sync_lookback_days=365,
        bill_batch_vote_sync_max_target_bills=300,

        bill_batch_max_ai_per_run=200,
        bill_batch_ai_max_retry_count=3,
        bill_batch_ai_retry_wait_minutes=60,
        bill_batch_ai_processing_timeout_minutes=30,
        bill_batch_ai_quota_retry_wait_minutes=720,

        min_proposal_date=os.getenv("MIN_PROPOSAL_DATE", "2026-01-01"),

        prompt_version="dry-run",
        image_tag="dry-run",
        git_commit_sha="dry-run",
    )


def main():
    settings = _build_dry_run_settings()
    print(f"[DRY_RUN] base_url={settings.bill_api_base_url}")
    print(f"[DRY_RUN] AGE={settings.bill_start_ord}")
    print(f"[DRY_RUN] page_size={settings.bill_batch_page_size}, max_pages={settings.bill_batch_max_pages}")
    print(f"[DRY_RUN] min_proposal_date={settings.min_proposal_date}")
    print()

    client = BillInfoApiClient(settings)

    bills_after_cutoff = 0
    bills_filtered = 0
    sample_count = 0
    min_date_cutoff = settings.min_proposal_date  # str

    for bill in client.iter_bills():
        proposal_date = bill.get("proposal_date")
        if proposal_date and proposal_date.isoformat() < min_date_cutoff:
            bills_filtered += 1
            continue

        bills_after_cutoff += 1

        if sample_count < 3:
            sample_count += 1
            print(f"--- SAMPLE #{sample_count} ---")
            print(f"  external_bill_id: {bill.get('external_bill_id')}")
            print(f"  bill_no:          {bill.get('bill_no')}")
            print(f"  official_title:   {bill.get('official_title')[:60]}")
            print(f"  proposal_date:    {bill.get('proposal_date')}")
            print(f"  proposer_kind:    {bill.get('proposer_kind')}")
            print(f"  proposer_name:    {bill.get('representative_proposer_name')}")
            print(f"  proposer_count:   {bill.get('proposer_count')}")
            print(f"  pass_gubn:        {bill.get('current_pass_gubn')}")
            print(f"  general_result:   {bill.get('current_general_result')}")
            print(f"  stage_code:       {bill.get('current_proc_stage_code')}")
            print(f"  stage_name:       {bill.get('current_proc_stage_name')}")
            print(f"  stage_order:      {bill.get('current_proc_stage_order')}")
            print(f"  status_proc_date: {bill.get('status_proc_date')}")
            print(f"  detail_url:       {bill.get('detail_url')}")
            print(f"  summary_raw in dict?: {'summary_raw' in bill}  (False 여야 옛 DB summary 보존됨)")
            print("  history entries:")
            for e in bill.get('_status_history_entries', []):
                print(f"    - {e['stage_code']:32s} {e['stage_name']:12s} date={e['proc_date']}")
            print()

    print(f"[DRY_RUN] 최종: 수집대상={bills_after_cutoff}, min_date 필터={bills_filtered}")


if __name__ == "__main__":
    main()
