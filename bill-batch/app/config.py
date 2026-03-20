import os
from dataclasses import dataclass
from typing import List


def _collect_gemini_keys() -> List[str]:
    keys = []

    csv_keys = os.getenv("GEMINI_API_KEYS", "").strip()
    if csv_keys:
        keys.extend([k.strip() for k in csv_keys.split(",") if k.strip()])

    default_key = os.getenv("GEMINI_API_KEY", "").strip()
    if default_key:
        keys.append(default_key)

    for i in range(1, 30):
        key = os.getenv(f"GEMINI_API_KEY_{i}", "").strip()
        if key:
            keys.append(key)

    seen = set()
    deduped = []
    for key in keys:
        if key not in seen:
            deduped.append(key)
            seen.add(key)

    return deduped


@dataclass
class Settings:
    db_host: str
    db_port: int
    db_name: str
    db_username: str
    db_password: str
    db_schema: str

    bill_service_key: str
    assembly_service_key: str

    gemini_keys: List[str]
    gemini_model: str
    gemini_temperature: float
    prompt_version: str

    bill_batch_job_type: str
    bill_batch_trigger_type: str
    bill_batch_lookback_days: int
    bill_batch_page_size: int
    bill_batch_sleep_ms: int
    bill_batch_ai_sleep_ms: int

    image_tag: str
    git_commit_sha: str

    @classmethod
    def from_env(cls) -> "Settings":
        db_password = os.getenv("DB_PASSWORD", "").strip()
        if not db_password:
            raise ValueError("DB_PASSWORD is required")

        bill_service_key = os.getenv("BILL_SERVICE_KEY", "").strip()
        if not bill_service_key:
            raise ValueError("BILL_SERVICE_KEY is required")

        gemini_keys = _collect_gemini_keys()
        if not gemini_keys:
            raise ValueError("At least one GEMINI_API_KEY is required")

        return cls(
            db_host=os.getenv("DB_HOST", "localhost").strip(),
            db_port=int(os.getenv("DB_PORT", "5432").strip()),
            db_name=os.getenv("DB_NAME", "mypoly").strip(),
            db_username=os.getenv("DB_USERNAME", os.getenv("DB_USER", "mypoly")).strip(),
            db_password=db_password,
            db_schema=os.getenv("DB_SCHEMA", "bill_service").strip(),
            bill_service_key=bill_service_key,
            assembly_service_key=os.getenv("ASSEMBLY_SERVICE_KEY", "").strip(),
            gemini_keys=gemini_keys,
            gemini_model=os.getenv("GEMINI_MODEL", "gemini-1.5-flash").strip(),
            gemini_temperature=float(os.getenv("GEMINI_TEMPERATURE", "0.2").strip()),
            prompt_version=os.getenv("PROMPT_VERSION", "v1-mypoly-17cats").strip(),
            bill_batch_job_type=os.getenv("BILL_BATCH_JOB_TYPE", "DAILY_BILL_COLLECT").strip(),
            bill_batch_trigger_type=os.getenv("BILL_BATCH_TRIGGER_TYPE", "MANUAL").strip(),
            bill_batch_lookback_days=int(os.getenv("BILL_BATCH_LOOKBACK_DAYS", "2").strip()),
            bill_batch_page_size=int(os.getenv("BILL_BATCH_PAGE_SIZE", "100").strip()),
            bill_batch_sleep_ms=int(os.getenv("BILL_BATCH_SLEEP_MS", "150").strip()),
            bill_batch_ai_sleep_ms=int(os.getenv("BILL_BATCH_AI_SLEEP_MS", "1000").strip()),
            image_tag=os.getenv("IMAGE_TAG", "local").strip(),
            git_commit_sha=os.getenv("GIT_COMMIT_SHA", "local").strip(),
        )