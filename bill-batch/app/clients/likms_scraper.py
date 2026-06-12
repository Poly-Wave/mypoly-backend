import asyncio
from typing import Dict, List, Tuple

_LIKMS_URL = "https://likms.assembly.go.kr/bill/bi/billDetailPage.do?billId={bill_id}"
_SUMMARY_SELECTOR = "#prntSummary"


async def _run_scrape(bills: List[Tuple[int, str]], concurrency: int) -> Dict[int, str]:
    from playwright.async_api import async_playwright

    results: Dict[int, str] = {}
    semaphore = asyncio.Semaphore(concurrency)

    async def scrape_one(bill_id: int, external_bill_id: str):
        async with semaphore:
            page = await browser.new_page()
            try:
                url = _LIKMS_URL.format(bill_id=external_bill_id)
                await page.goto(url, timeout=30000, wait_until="domcontentloaded")
                try:
                    await page.wait_for_selector(_SUMMARY_SELECTOR, timeout=15000)
                except Exception:
                    pass
                element = await page.query_selector(_SUMMARY_SELECTOR)
                if element:
                    text = (await element.text_content() or "").strip()
                    if text:
                        results[bill_id] = text
                        print(
                            f"[SCRAPE] 성공 bill_id={bill_id} external_bill_id={external_bill_id} len={len(text)}",
                            flush=True,
                        )
                    else:
                        print(
                            f"[SCRAPE] 빈 내용 bill_id={bill_id} external_bill_id={external_bill_id}",
                            flush=True,
                        )
                else:
                    print(
                        f"[SCRAPE] 선택자 없음 bill_id={bill_id} external_bill_id={external_bill_id}",
                        flush=True,
                    )
            except Exception as exc:
                print(
                    f"[SCRAPE][오류] bill_id={bill_id} external_bill_id={external_bill_id} error={exc}",
                    flush=True,
                )
            finally:
                await page.close()

    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        try:
            await asyncio.gather(*[scrape_one(bill_id, ext_id) for bill_id, ext_id in bills])
        finally:
            await browser.close()

    return results


class LikmsScraper:
    def __init__(self, concurrency: int = 3):
        if concurrency < 1:
            raise ValueError(f"BILL_BATCH_SCRAPE_CONCURRENCY는 1 이상이어야 합니다: {concurrency}")
        self.concurrency = concurrency

    def scrape(self, bills: List[Tuple[int, str]]) -> Dict[int, str]:
        if not bills:
            return {}
        return asyncio.run(_run_scrape(bills, self.concurrency))
