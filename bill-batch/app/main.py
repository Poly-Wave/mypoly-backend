import argparse
import traceback
from datetime import datetime

from dotenv import load_dotenv

from app.config import Settings
from app.services.batch_runner import BatchRunner


def parse_args():
    parser = argparse.ArgumentParser(description="mypoly bill-batch runner")
    parser.add_argument("--from-date", type=str, default=None, help="YYYY-MM-DD")
    parser.add_argument("--to-date", type=str, default=None, help="YYYY-MM-DD")
    parser.add_argument("--trigger-type", type=str, default=None, help="CRON | MANUAL | RETRY")
    return parser.parse_args()


def parse_date(value: str | None):
    if not value:
        return None
    return datetime.strptime(value, "%Y-%m-%d").date()


def main():
    load_dotenv()

    args = parse_args()
    settings = Settings.from_env()

    try:
        runner = BatchRunner(settings)
        result = runner.run(
            from_date=parse_date(args.from_date),
            to_date=parse_date(args.to_date),
            trigger_type=args.trigger_type,
        )
        print(result, flush=True)
    except Exception as exc:
        print(f"[FATAL] {exc}", flush=True)
        traceback.print_exc()
        raise


if __name__ == "__main__":
    main()
