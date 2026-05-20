-- 시스템 정책 HOME_NEW_INTEREST_AGENDA_DAILY 의 title/body 에서
-- 가독성 떨어지는 토큰명 {8} 을 {개수} 로 교체한다.
--
-- - SystemNotificationPolicySeeder 는 멱등(없을 때만 insert)이라 이미 시드된 row 는 자동으로 갱신되지 않는다.
-- - 운영자가 PATCH 로 본문을 수정해 토큰이 다른 형태로 바뀐 경우엔 REPLACE 가 no-op 가 되어 안전하다.
-- - 다른 정책(다른 policy_key) 의 본문은 건드리지 않는다.

UPDATE notification_service.notification_policies
   SET title = REPLACE(title, '{8}', '{개수}'),
       body  = REPLACE(body,  '{8}', '{개수}'),
       updated_at = now()
 WHERE policy_key = 'HOME_NEW_INTEREST_AGENDA_DAILY';
