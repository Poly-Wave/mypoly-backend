from typing import Any, Dict, List

from psycopg2.extras import Json


class AiRepository:
    def __init__(self, conn, schema: str):
        self.conn = conn
        self.schema = schema

    def get_current_analysis(self, bill_id: int):
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT *
                FROM {self.schema}.bill_ai_analyses
                WHERE bill_id = %s
                  AND is_current = TRUE
                """,
                (bill_id,),
            )
            return cur.fetchone()

    def get_next_analysis_version(self, bill_id: int) -> int:
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT COALESCE(MAX(analysis_version), 0) + 1 AS next_version
                FROM {self.schema}.bill_ai_analyses
                WHERE bill_id = %s
                """,
                (bill_id,),
            )
            row = cur.fetchone()
            return int(row["next_version"])

    def insert_failed_analysis(
        self,
        bill_id: int,
        source_text_hash: str | None,
        model_name: str,
        prompt_version: str,
        temperature: float,
        analysis_input: Dict[str, Any],
        error_message: str,
    ) -> int:
        version = self.get_next_analysis_version(bill_id)

        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                INSERT INTO {self.schema}.bill_ai_analyses (
                    bill_id,
                    analysis_version,
                    analysis_status,
                    headline,
                    summary,
                    source_text_hash,
                    model_name,
                    prompt_version,
                    temperature,
                    analysis_input,
                    analysis_output,
                    error_message,
                    is_current,
                    generated_at,
                    created_at
                ) VALUES (
                    %s, %s, 'FAILED',
                    NULL, NULL, %s, %s, %s, %s,
                    %s, NULL, %s, FALSE, now(), now()
                )
                RETURNING id
                """,
                (
                    bill_id,
                    version,
                    source_text_hash,
                    model_name,
                    prompt_version,
                    temperature,
                    Json(analysis_input),
                    error_message[:5000],
                ),
            )
            return cur.fetchone()["id"]

    def insert_success_analysis(
        self,
        bill_id: int,
        source_text_hash: str | None,
        model_name: str,
        prompt_version: str,
        temperature: float,
        analysis_input: Dict[str, Any],
        analysis_output: Dict[str, Any],
        category_ids: List[int],
        axis_for: Dict[str, int],
        axis_against: Dict[str, int],
    ) -> int:
        version = self.get_next_analysis_version(bill_id)

        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                UPDATE {self.schema}.bill_ai_analyses
                SET is_current = FALSE
                WHERE bill_id = %s
                  AND is_current = TRUE
                """,
                (bill_id,),
            )

            cur.execute(
                f"""
                INSERT INTO {self.schema}.bill_ai_analyses (
                    bill_id,
                    analysis_version,
                    analysis_status,
                    headline,
                    summary,
                    source_text_hash,
                    model_name,
                    prompt_version,
                    temperature,
                    analysis_input,
                    analysis_output,
                    error_message,
                    is_current,
                    generated_at,
                    created_at
                ) VALUES (
                    %s, %s, 'SUCCESS',
                    %s, %s, %s, %s, %s, %s,
                    %s, %s, NULL, TRUE, now(), now()
                )
                RETURNING id
                """,
                (
                    bill_id,
                    version,
                    analysis_output["headline"],
                    analysis_output["summary"],
                    source_text_hash,
                    model_name,
                    prompt_version,
                    temperature,
                    Json(analysis_input),
                    Json(analysis_output),
                ),
            )
            analysis_id = cur.fetchone()["id"]

            rank = 1
            for category_id in category_ids:
                cur.execute(
                    f"""
                    INSERT INTO {self.schema}.bill_ai_categories (
                        analysis_id,
                        category_id,
                        rank_order,
                        created_at
                    ) VALUES (%s, %s, %s, now())
                    """,
                    (analysis_id, category_id, rank),
                )
                rank += 1

            for axis_code, weight in axis_for.items():
                cur.execute(
                    f"""
                    INSERT INTO {self.schema}.bill_ai_axis_weights (
                        analysis_id,
                        opinion_type,
                        axis_code,
                        weight,
                        created_at
                    ) VALUES (%s, 'FOR', %s, %s, now())
                    """,
                    (analysis_id, axis_code, int(weight)),
                )

            for axis_code, weight in axis_against.items():
                cur.execute(
                    f"""
                    INSERT INTO {self.schema}.bill_ai_axis_weights (
                        analysis_id,
                        opinion_type,
                        axis_code,
                        weight,
                        created_at
                    ) VALUES (%s, 'AGAINST', %s, %s, now())
                    """,
                    (analysis_id, axis_code, int(weight)),
                )

        return analysis_id