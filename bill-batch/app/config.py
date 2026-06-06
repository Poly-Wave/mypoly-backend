import os
from dataclasses import dataclass
from typing import List


def _get_env(name: str, default: str | None = None) -> str | None:
    value = os.getenv(name)
    if value is None:
        return default
    value = value.strip()
    return value if value != "" else default


def _require_env(*names: str) -> str:
    for name in names:
        value = _get_env(name)
        if value:
            return value
    joined = ", ".join(names)
    raise ValueError(f"필수 환경변수가 없습니다. 다음 중 하나는 반드시 설정해야 합니다: [{joined}]")


def _parse_bool(value: str | None, default: bool = False) -> bool:
    if value is None:
        return default
    return value.strip().lower() in {"1", "true", "y", "yes", "on"}


def _load_gemini_keys(max_keys: int = 40) -> List[str]:
    keys: List[str] = []

    for i in range(1, max_keys + 1):
        value = _get_env(f"GEMINI_API_KEY_{i}")
        if value:
            keys.append(value)

    single_key = _get_env("GEMINI_API_KEY")
    if single_key:
        keys.append(single_key)

    deduped: List[str] = []
    seen = set()
    for key in keys:
        if key not in seen:
            deduped.append(key)
            seen.add(key)

    return deduped


@dataclass(frozen=True)
class Settings:
    db_host: str
    db_port: int
    db_name: str
    db_username: str
    db_password: str
    db_schema: str

    bill_api_base_url: str
    bill_start_ord: int
    bill_end_ord: int

    assembly_service_key: str
    bill_batch_member_age: int
    bill_batch_member_sync_page_size: int
    bill_batch_member_sync_max_pages: int

    gemini_keys: List[str]
    gemini_model: str
    gemini_temperature: float

    bill_batch_job_type: str
    bill_batch_trigger_type: str
    bill_batch_page_size: int
    bill_batch_max_pages: int
    bill_batch_request_timeout_sec: int
    bill_batch_sleep_ms: int
    bill_batch_ai_sleep_ms: int

    bill_batch_enable_bill_collect: bool
    bill_batch_enable_member_sync: bool
    bill_batch_enable_vote_sync: bool
    bill_batch_enable_ai: bool

    bill_batch_vote_sync_missing_only: bool
    bill_batch_vote_sync_lookback_days: int
    bill_batch_vote_sync_max_target_bills: int

    bill_batch_max_ai_per_run: int
    bill_batch_ai_max_retry_count: int
    bill_batch_ai_retry_wait_minutes: int
    bill_batch_ai_processing_timeout_minutes: int
    bill_batch_ai_quota_retry_wait_minutes: int

    min_proposal_date: str

    prompt_version: str
    image_tag: str
    git_commit_sha: str

    @property
    def db_user(self) -> str:
        return self.db_username

    @classmethod
    def from_env(cls, require_gemini_keys: bool | None = None) -> "Settings":
        enable_ai = _parse_bool(_get_env("BILL_BATCH_ENABLE_AI", "true"), default=True)

        if require_gemini_keys is None:
            require_gemini_keys = enable_ai

        gemini_keys = _load_gemini_keys(max_keys=40)
        if require_gemini_keys and not gemini_keys:
            raise ValueError("GEMINI_API_KEY 또는 GEMINI_API_KEY_* 중 최소 1개는 반드시 설정해야 합니다")

        return cls(
            db_host=_require_env("DB_HOST"),
            db_port=int(_get_env("DB_PORT", "5432")),
            db_name=_require_env("DB_NAME"),
            db_username=_require_env("DB_USERNAME", "DB_USER"),
            db_password=_require_env("DB_PASSWORD"),
            db_schema=_get_env("DB_SCHEMA", "bill_service"),

            # 의안 API base URL.
            # 옛 data.go.kr / BillInfoService2 가 deprecated 되어 신 API (open.assembly.go.kr/TVBPMBILL11) 사용.
            # 인증키는 ASSEMBLY_SERVICE_KEY 재사용 (옛 BILL_SERVICE_KEY 는 폐기).
            bill_api_base_url=_get_env(
                "BILL_API_BASE_URL",
                "https://open.assembly.go.kr/portal/openapi",
            ),
            bill_start_ord=int(_get_env("BILL_START_ORD", "22")),
            bill_end_ord=int(_get_env("BILL_END_ORD", "22")),

            assembly_service_key=_require_env("ASSEMBLY_SERVICE_KEY"),
            bill_batch_member_age=int(_get_env("BILL_BATCH_MEMBER_AGE", "22")),
            bill_batch_member_sync_page_size=int(_get_env("BILL_BATCH_MEMBER_SYNC_PAGE_SIZE", "300")),
            bill_batch_member_sync_max_pages=int(_get_env("BILL_BATCH_MEMBER_SYNC_MAX_PAGES", "10")),

            gemini_keys=gemini_keys,
            gemini_model=_get_env("GEMINI_MODEL", "gemini-2.5-flash"),
            gemini_temperature=float(_get_env("GEMINI_TEMPERATURE", "0.2")),

            bill_batch_job_type=_get_env("BILL_BATCH_JOB_TYPE", "DAILY_BILL_PIPELINE"),
            bill_batch_trigger_type=_get_env("BILL_BATCH_TRIGGER_TYPE", "CRON"),
            bill_batch_page_size=int(_get_env("BILL_BATCH_PAGE_SIZE", "100")),
            bill_batch_max_pages=int(_get_env("BILL_BATCH_MAX_PAGES", "300")),
            bill_batch_request_timeout_sec=int(_get_env("BILL_BATCH_REQUEST_TIMEOUT_SEC", "30")),
            bill_batch_sleep_ms=int(_get_env("BILL_BATCH_SLEEP_MS", "150")),
            bill_batch_ai_sleep_ms=int(_get_env("BILL_BATCH_AI_SLEEP_MS", "1000")),

            bill_batch_enable_bill_collect=_parse_bool(_get_env("BILL_BATCH_ENABLE_BILL_COLLECT", "true"), default=True),
            bill_batch_enable_member_sync=_parse_bool(_get_env("BILL_BATCH_ENABLE_MEMBER_SYNC", "false"), default=False),
            bill_batch_enable_vote_sync=_parse_bool(_get_env("BILL_BATCH_ENABLE_VOTE_SYNC", "true"), default=True),
            bill_batch_enable_ai=enable_ai,

            bill_batch_vote_sync_missing_only=_parse_bool(_get_env("BILL_BATCH_VOTE_SYNC_MISSING_ONLY", "true"), default=True),
            bill_batch_vote_sync_lookback_days=int(_get_env("BILL_BATCH_VOTE_SYNC_LOOKBACK_DAYS", "365")),
            bill_batch_vote_sync_max_target_bills=int(_get_env("BILL_BATCH_VOTE_SYNC_MAX_TARGET_BILLS", "300")),

            bill_batch_max_ai_per_run=int(_get_env("BILL_BATCH_MAX_AI_PER_RUN", "200")),
            bill_batch_ai_max_retry_count=int(_get_env("BILL_BATCH_AI_MAX_RETRY_COUNT", "3")),
            bill_batch_ai_retry_wait_minutes=int(_get_env("BILL_BATCH_AI_RETRY_WAIT_MINUTES", "60")),
            bill_batch_ai_processing_timeout_minutes=int(_get_env("BILL_BATCH_AI_PROCESSING_TIMEOUT_MINUTES", "30")),
            bill_batch_ai_quota_retry_wait_minutes=int(_get_env("BILL_BATCH_AI_QUOTA_RETRY_WAIT_MINUTES", "720")),

            min_proposal_date=_get_env("MIN_PROPOSAL_DATE", "2026-01-01"),

            prompt_version=_get_env("PROMPT_VERSION", "v1-mypoly-17cats"),
            image_tag=_get_env("IMAGE_TAG", "unknown"),
            git_commit_sha=_get_env("GIT_COMMIT_SHA", "unknown"),
        )


def load_settings(require_gemini_keys: bool | None = None) -> Settings:
    return Settings.from_env(require_gemini_keys=require_gemini_keys)