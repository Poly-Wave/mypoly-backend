from psycopg2.extras import Json


class BatchRepository:
    def __init__(self, conn, schema: str):
        self.conn = conn
        self.schema = schema

    def create_run(self, job_type: str, trigger_type: str, image_tag: str, git_commit_sha: str) -> int:
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                INSERT INTO {self.schema}.bill_batch_runs (
                    job_type,
                    trigger_type,
                    run_status,
                    started_at,
                    image_tag,
                    git_commit_sha,
                    records_found,
                    records_inserted,
                    records_updated,
                    records_skipped,
                    records_failed,
                    created_at
                ) VALUES (
                    %s, %s, 'RUNNING', now(), %s, %s, 0, 0, 0, 0, 0, now()
                )
                RETURNING id
                """,
                (job_type, trigger_type, image_tag, git_commit_sha),
            )
            return cur.fetchone()["id"]

    def add_item(
        self,
        batch_run_id: int,
        item_type: str,
        target_external_id: str | None,
        target_internal_id: int | None,
        item_status: str,
        action_type: str | None,
        error_message: str | None = None,
        payload: dict | None = None,
    ):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                INSERT INTO {self.schema}.bill_batch_run_items (
                    batch_run_id,
                    item_type,
                    target_external_id,
                    target_internal_id,
                    item_status,
                    action_type,
                    error_message,
                    payload,
                    created_at
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, now())
                """,
                (
                    batch_run_id,
                    item_type,
                    target_external_id,
                    target_internal_id,
                    item_status,
                    action_type,
                    error_message[:5000] if error_message else None,
                    Json(payload or {}),
                ),
            )

    def finish_run(
        self,
        batch_run_id: int,
        run_status: str,
        records_found: int,
        records_inserted: int,
        records_updated: int,
        records_skipped: int,
        records_failed: int,
        message: str,
    ):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bill_batch_runs
                SET
                    run_status = %s,
                    finished_at = now(),
                    records_found = %s,
                    records_inserted = %s,
                    records_updated = %s,
                    records_skipped = %s,
                    records_failed = %s,
                    message = %s
                WHERE id = %s
                """,
                (
                    run_status,
                    records_found,
                    records_inserted,
                    records_updated,
                    records_skipped,
                    records_failed,
                    message,
                    batch_run_id,
                ),
            )