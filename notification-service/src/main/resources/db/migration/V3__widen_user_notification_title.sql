-- user_notifications.title 길이 확대 (200 → 500).
--
-- 사유: 행 6/7 의 정책 title 에 {안건 제목} 토큰이 들어가는데,
--       bill-service 의 Bill.official_title 은 최대 500자다.
--       치환 후 200자를 초과하면 INSERT 시 길이 제한 오류가 발생할 수 있어 컬럼을 맞춘다.
-- 참고: 발급 시점에 application 레이어에서도 컬럼 한계로 truncate 하는 2차 방어가 있다.

ALTER TABLE notification_service.user_notifications
    ALTER COLUMN title TYPE VARCHAR(500);
