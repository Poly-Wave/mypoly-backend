import os
import sys
from collections import Counter
from datetime import datetime
from pathlib import Path

# 프로젝트 루트(app 패키지) import 가능하게 경로 추가
CURRENT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = CURRENT_DIR.parent
sys.path.insert(0, str(PROJECT_ROOT))

from app.config import load_settings
from app.clients.bill_info_api_client import BillInfoApiClient


def parse_yyyy_mm_dd(value: str):
    return datetime.strptime(value, "%Y-%m-%d").date()


def main():
    from_date_str = os.getenv("TEST_FROM_DATE", "2026-03-18")
    to_date_str = os.getenv("TEST_TO_DATE", "2026-03-19")
    max_print = int(os.getenv("TEST_MAX_PRINT", "50"))
    max_collect = int(os.getenv("TEST_MAX_COLLECT", "300"))

    from_date = parse_yyyy_mm_dd(from_date_str)
    to_date = parse_yyyy_mm_dd(to_date_str)

    settings = load_settings(require_gemini_keys=False)
    client = BillInfoApiClient(settings)

    print(f"[TEST] from_date={from_date} to_date={to_date}")
    print(f"[TEST] min_proposal_date={settings.min_proposal_date}")
    print(f"[TEST] start_ord={settings.bill_start_ord} end_ord={settings.bill_end_ord}")
    print(f"[TEST] page_size={settings.bill_batch_page_size} max_pages={settings.bill_batch_max_pages}")
    print("")

    rows = []
    proposal_date_counter = Counter()

    for idx, bill in enumerate(client.iter_bills(from_date, to_date), start=1):
        proposal_date = bill.get("proposal_date")
        bill_no = bill.get("bill_no")
        title = bill.get("official_title")
        external_bill_id = bill.get("external_bill_id")

        proposal_date_counter[str(proposal_date)] += 1
        rows.append(
            {
                "idx": idx,
                "proposal_date": proposal_date,
                "bill_no": bill_no,
                "external_bill_id": external_bill_id,
                "official_title": title,
            }
        )

        if idx <= max_print:
            print(
                f"{idx:03d} | proposal_date={proposal_date} | bill_no={bill_no} "
                f"| external_bill_id={external_bill_id} | title={title}"
            )

        if idx >= max_collect:
            print("")
            print(f"[TEST] reached max_collect={max_collect}, stop")
            break

    print("")
    print("=== SUMMARY ===")
    print(f"total_collected={len(rows)}")
    print("proposal_date_distribution:")

    for proposal_date, count in sorted(proposal_date_counter.items(), reverse=True):
        print(f"  {proposal_date}: {count}")

    print("")
    print("=== RANGE CHECK ===")
    in_range = 0
    out_of_range = 0
    null_dates = 0

    for row in rows:
        pd = row["proposal_date"]
        if pd is None:
            null_dates += 1
        elif from_date <= pd <= to_date:
            in_range += 1
        else:
            out_of_range += 1

    print(f"in_range={in_range}")
    print(f"out_of_range={out_of_range}")
    print(f"null_dates={null_dates}")


if __name__ == "__main__":
    main()