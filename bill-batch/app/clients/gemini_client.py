import json
import time
from dataclasses import dataclass
from typing import Any, Dict, List

import requests

from app.config import Settings


SYSTEM_PROMPT = """
너는 한국 국회 의안을 쉬운 말로 요약하는 편집자다.

반드시 JSON 객체 하나만 반환한다.
절대 코드블록, 설명문, 마크다운을 출력하지 않는다.

출력 형식:
{
  "headline": "문자열",
  "summary": "문자열",
  "categories": ["카테고리코드"],
  "vote": {
    "for": {"P": 1, "U": 1},
    "against": {"M": 1, "R": 1}
  }
}

카테고리는 아래 17개 중 1~2개만 선택:
DIGITAL
SECURITY
BROADCAST
ECONOMY
REAL_ESTATE
TRANSPORT
ENVIRONMENT
MEDICAL
WELFARE
EDUCATION
LABOR
WOMEN
FAMILY
CHILD
SEX_CRIME
FOREIGN_DEFENSE
LAW_ADMIN

축 코드는 아래만 허용:
P, M, U, T, N, S, O, R

규칙:
- headline: 20~36자 내외, 중립적이고 쉬운 뉴스형 문장
- summary: 2~3문장, 과장 없이 쉬운 설명
- categories: 1개 권장, 정말 애매할 때만 2개
- vote.for / vote.against 는 각 1~3개 축만 사용
- 같은 축쌍(P/M, U/T, N/S, O/R)을 같은 쪽에 동시에 넣지 않는다
- 값은 항상 정수 1
- 입력에 없는 사실을 추정하지 않는다
- 법률안, 개정법률안 같은 형식 단어는 headline/summary에서 남발하지 않는다
""".strip()


@dataclass
class GeminiApiError(Exception):
    code: str
    message: str
    retryable: bool
    quota_exhausted: bool = False

    def __str__(self) -> str:
        return f"{self.code}: {self.message}"


class GeminiClient:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.keys = settings.gemini_keys
        self.index = 0

    def _next_key(self) -> str:
        key = self.keys[self.index % len(self.keys)]
        self.index += 1
        return key

    def _extract_json_text(self, text: str) -> str:
        text = text.strip()

        if text.startswith("```"):
            lines = text.splitlines()
            if len(lines) >= 3:
                text = "\n".join(lines[1:-1]).strip()

        start = text.find("{")
        end = text.rfind("}")
        if start == -1 or end == -1 or end < start:
            raise GeminiApiError(
                code="INVALID_RESPONSE",
                message=f"Gemini response is not valid JSON: {text[:500]}",
                retryable=True,
            )

        return text[start:end + 1]

    def _normalize_vote(self, vote_obj: Dict[str, Any]) -> Dict[str, Dict[str, int]]:
        allowed_axes = {"P", "M", "U", "T", "N", "S", "O", "R"}

        def normalize_side(side: Dict[str, Any]) -> Dict[str, int]:
            normalized = {}
            for key, value in (side or {}).items():
                try:
                    if key in allowed_axes and int(value) > 0:
                        normalized[key] = 1
                except Exception:
                    continue
            return normalized

        return {
            "for": normalize_side(vote_obj.get("for", {})),
            "against": normalize_side(vote_obj.get("against", {})),
        }

    def _build_payload(self, title: str, body: str) -> Dict[str, Any]:
        prompt = f"""
[입력 제목]
{title}

[입력 본문]
{body}
""".strip()

        return {
            "systemInstruction": {
                "parts": [{"text": SYSTEM_PROMPT}]
            },
            "contents": [
                {
                    "role": "user",
                    "parts": [{"text": prompt}]
                }
            ],
            "generationConfig": {
                "temperature": self.settings.gemini_temperature,
                "responseMimeType": "application/json",
            }
        }

    def _classify_http_error(self, response: requests.Response) -> GeminiApiError:
        body_head = response.text[:500]

        if response.status_code == 429:
            return GeminiApiError(
                code="RESOURCE_EXHAUSTED",
                message=f"status=429 body={body_head}",
                retryable=True,
                quota_exhausted=True,
            )

        if response.status_code == 400 and "API_KEY_INVALID" in response.text:
            return GeminiApiError(
                code="API_KEY_INVALID",
                message=f"status=400 body={body_head}",
                retryable=False,
            )

        if response.status_code in (500, 502, 503, 504):
            return GeminiApiError(
                code="TEMPORARY_SERVER_ERROR",
                message=f"status={response.status_code} body={body_head}",
                retryable=True,
            )

        if 400 <= response.status_code < 500:
            return GeminiApiError(
                code="INVALID_REQUEST",
                message=f"status={response.status_code} body={body_head}",
                retryable=False,
            )

        return GeminiApiError(
            code="HTTP_ERROR",
            message=f"status={response.status_code} body={body_head}",
            retryable=True,
        )

    def analyze_bill(self, title: str, body: str) -> Dict[str, Any]:
        model = self.settings.gemini_model or "gemini-2.5-flash"
        url = f"https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent"
        payload = self._build_payload(title=title, body=body)

        errors: List[GeminiApiError] = []

        for attempt in range(len(self.keys)):
            api_key = self._next_key()
            headers = {
                "x-goog-api-key": api_key,
                "Content-Type": "application/json",
            }

            masked_key = f"{api_key[:6]}...{api_key[-4:]}" if len(api_key) >= 10 else "***"
            print(
                f"[GEMINI] request attempt={attempt + 1}/{len(self.keys)} model={model} key={masked_key}",
                flush=True,
            )

            try:
                response = requests.post(
                    url,
                    headers=headers,
                    json=payload,
                    timeout=60,
                )

                print(
                    f"[GEMINI] response status={response.status_code} body_head={response.text[:300]!r}",
                    flush=True,
                )

                if response.status_code >= 400:
                    err = self._classify_http_error(response)
                    errors.append(err)
                    time.sleep(self.settings.bill_batch_ai_sleep_ms / 1000.0)
                    continue

                data = response.json()

                candidates = data.get("candidates") or []
                if not candidates:
                    raise GeminiApiError(
                        code="INVALID_RESPONSE",
                        message=f"No candidates in Gemini response: {str(data)[:500]}",
                        retryable=True,
                    )

                content = candidates[0].get("content") or {}
                parts = content.get("parts") or []
                if not parts:
                    raise GeminiApiError(
                        code="INVALID_RESPONSE",
                        message=f"No parts in Gemini response: {str(data)[:500]}",
                        retryable=True,
                    )

                text = str(parts[0].get("text", "")).strip()
                if not text:
                    raise GeminiApiError(
                        code="INVALID_RESPONSE",
                        message=f"Empty text in Gemini response: {str(data)[:500]}",
                        retryable=True,
                    )

                json_text = self._extract_json_text(text)
                parsed = json.loads(json_text)

                headline = str(parsed.get("headline", "")).strip()
                summary = str(parsed.get("summary", "")).strip()
                categories = parsed.get("categories", []) or []
                categories = [str(v).strip() for v in categories if str(v).strip()]

                if not headline:
                    raise GeminiApiError(code="INVALID_RESPONSE", message="headline is empty", retryable=True)
                if not summary:
                    raise GeminiApiError(code="INVALID_RESPONSE", message="summary is empty", retryable=True)
                if not categories:
                    raise GeminiApiError(code="INVALID_RESPONSE", message="categories is empty", retryable=True)

                return {
                    "headline": headline,
                    "summary": summary,
                    "categories": categories[:2],
                    "vote": self._normalize_vote(parsed.get("vote", {})),
                    "raw_response": parsed,
                }

            except GeminiApiError as exc:
                errors.append(exc)
                print(f"[GEMINI][ERROR] {exc}", flush=True)
                time.sleep(self.settings.bill_batch_ai_sleep_ms / 1000.0)
                continue
            except requests.RequestException as exc:
                err = GeminiApiError(
                    code="NETWORK_ERROR",
                    message=str(exc),
                    retryable=True,
                )
                errors.append(err)
                print(f"[GEMINI][ERROR] {err}", flush=True)
                time.sleep(self.settings.bill_batch_ai_sleep_ms / 1000.0)
                continue
            except Exception as exc:
                err = GeminiApiError(
                    code="UNEXPECTED_ERROR",
                    message=str(exc),
                    retryable=True,
                )
                errors.append(err)
                print(f"[GEMINI][ERROR] {err}", flush=True)
                time.sleep(self.settings.bill_batch_ai_sleep_ms / 1000.0)
                continue

        if any(err.quota_exhausted for err in errors):
            raise GeminiApiError(
                code="RESOURCE_EXHAUSTED",
                message=f"All Gemini keys exhausted or quota blocked. last={errors[-1] if errors else 'unknown'}",
                retryable=True,
                quota_exhausted=True,
            )

        non_retryable = [err for err in errors if not err.retryable]
        if non_retryable:
            last = non_retryable[-1]
            raise GeminiApiError(
                code=last.code,
                message=last.message,
                retryable=False,
            )

        last = errors[-1] if errors else GeminiApiError(
            code="UNKNOWN_ERROR",
            message="Gemini request failed without detailed error",
            retryable=True,
        )
        raise GeminiApiError(
            code=last.code,
            message=last.message,
            retryable=last.retryable,
            quota_exhausted=last.quota_exhausted,
        )