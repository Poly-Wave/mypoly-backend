from typing import Dict


class CategoryRepository:
    def __init__(self, conn, schema: str):
        self.conn = conn
        self.schema = schema

    def get_active_category_map(self) -> Dict[str, int]:
        with self.conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT id, code
                FROM {self.schema}.bill_categories
                WHERE is_active = TRUE
                """
            )
            rows = cur.fetchall()

        return {row["code"]: row["id"] for row in rows}