import argparse
import traceback

from dotenv import load_dotenv

from app.config import load_settings
from app.services.batch_runner import BatchRunner


def parse_args():
    parser = argparse.ArgumentParser(description="mypoly 의안 배치 실행기")
    parser.add_argument("--trigger-type", type=str, default=None, help="CRON | MANUAL | RETRY")
    return parser.parse_args()


def main():
    load_dotenv()

    try:
        args = parse_args()
        settings = load_settings()

        runner = BatchRunner(settings)
        result = runner.run(
            trigger_type=args.trigger_type,
        )
        print(result, flush=True)

    except Exception as exc:
        print(f"[오류] {exc}", flush=True)
        traceback.print_exc()
        raise


if __name__ == "__main__":
    main()