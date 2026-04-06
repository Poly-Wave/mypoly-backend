import psycopg2
from psycopg2.extras import RealDictCursor

from app.config import Settings

# 모든 API 응답과 배치 날짜 계산을 KST 기준으로 통일한다.
# DB 내부 저장은 TIMESTAMPTZ(UTC)로 유지되지만,
# 세션 timezone을 Asia/Seoul로 설정하면 now() 등 현재 시각 함수가
# KST 기준으로 동작하여 날짜 경계(자정) 오차 사고를 방지한다.
_SESSION_TIMEZONE = "Asia/Seoul"


def get_connection(settings: Settings):
    conn = psycopg2.connect(
        host=settings.db_host,
        port=settings.db_port,
        dbname=settings.db_name,
        user=settings.db_username,
        password=settings.db_password,
        cursor_factory=RealDictCursor,
        options=f"-c TimeZone={_SESSION_TIMEZONE}",
    )
    return conn