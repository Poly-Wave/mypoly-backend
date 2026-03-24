import hashlib
import json


def sha256_hex(value: str | None) -> str | None:
    if value is None:
        return None
    normalized = value.strip()
    if not normalized:
        return None
    return hashlib.sha256(normalized.encode("utf-8")).hexdigest()


def hash_bill_ai_input(official_title: str | None, summary_raw: str | None) -> str:
    payload = {
        "official_title": (official_title or "").strip(),
        "summary_raw": (summary_raw or "").strip(),
    }
    raw = json.dumps(payload, ensure_ascii=False, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(raw.encode("utf-8")).hexdigest()