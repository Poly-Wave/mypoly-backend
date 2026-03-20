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
    raise ValueError(f"Environment variable one of [{joined}] is required")


def _load_gemini_keys(max_keys: int = 20) -> List[str]:
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
    bill_service_key: str

    gemini_keys: List[str]
    gemini_model: str
    gemini_temperature: float

    bill_batch_job_type: str
    bill_batch_trigger_type: str
    bill_batch_lookback_days: int
    bill_batch_page_size: int
    bill_batch_max_pages: int
    bill_batch_request_timeout_sec: int
    bill_batch_sleep_ms: int
    bill_batch_ai_sleep_ms: int

    prompt_version: str
    image_tag: str
    git_commit_sha: str

    @property
    def db_user(self) -> str:
        return self.db_username

    @classmethod
    def from_env(cls) -> "Settings":
        gemini_keys = _load_gemini_keys(max_keys=20)
        if not gemini_keys:
            raise ValueError("At least one GEMINI_API_KEY or GEMINI_API_KEY_* is required")

        return cls(
            db_host=_require_env("DB_HOST"),
            db_port=int(_get_env("DB_PORT", "5432")),
            db_name=_require_env("DB_NAME"),
            db_username=_require_env("DB_USERNAME", "DB_USER"),
            db_password=_require_env("DB_PASSWORD"),
            db_schema=_get_env("DB_SCHEMA", "bill_service"),

            bill_api_base_url=_get_env(
                "BILL_API_BASE_URL",
                "https://apis.data.go.kr/9710000/BillInfoService2",
            ),
            bill_service_key=_require_env("BILL_SERVICE_KEY"),

            gemini_keys=gemini_keys,
            gemini_model=_get_env("GEMINI_MODEL", "gemini-2.5-flash"),
            gemini_temperature=float(_get_env("GEMINI_TEMPERATURE", "0.2")),

            bill_batch_job_type=_get_env("BILL_BATCH_JOB_TYPE", "DAILY_BILL_COLLECT"),
            bill_batch_trigger_type=_get_env("BILL_BATCH_TRIGGER_TYPE", "CRON"),
            bill_batch_lookback_days=int(_get_env("BILL_BATCH_LOOKBACK_DAYS", "2")),
            bill_batch_page_size=int(_get_env("BILL_BATCH_PAGE_SIZE", "100")),
            bill_batch_max_pages=int(_get_env("BILL_BATCH_MAX_PAGES", "300")),
            bill_batch_request_timeout_sec=int(_get_env("BILL_BATCH_REQUEST_TIMEOUT_SEC", "30")),
            bill_batch_sleep_ms=int(_get_env("BILL_BATCH_SLEEP_MS", "200")),
            bill_batch_ai_sleep_ms=int(_get_env("BILL_BATCH_AI_SLEEP_MS", "300")),

            prompt_version=_get_env("PROMPT_VERSION", "v1"),
            image_tag=_get_env("IMAGE_TAG", "unknown"),
            git_commit_sha=_get_env("GIT_COMMIT_SHA", "unknown"),
        )


def load_settings() -> Settings:
    return Settings.from_env()