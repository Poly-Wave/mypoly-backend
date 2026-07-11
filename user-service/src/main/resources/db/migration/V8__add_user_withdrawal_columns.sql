SET search_path TO user_service;

-- 회원 탈퇴(soft-delete) 지원 컬럼.
-- 탈퇴 시 users 행은 보존하되 PII 를 말소하고 status=WITHDRAWN + withdrawn_at 를 기록한다.
-- 같은 uid 재활성(재가입 시 동일 user_id 재사용)과 탈퇴 후 7일 재가입 차단 판정에 사용한다.
-- (V6/V7 의 ADD COLUMN IF NOT EXISTS 멱등 패턴 준수)
ALTER TABLE users ADD COLUMN IF NOT EXISTS status             VARCHAR(10) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE users ADD COLUMN IF NOT EXISTS withdrawn_at       TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN IF NOT EXISTS withdrawal_reasons TEXT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS reason_etc         VARCHAR(200);

-- 7일 재가입 차단/탈퇴자 조회용
CREATE INDEX IF NOT EXISTS idx_users_status_withdrawn_at ON users (status, withdrawn_at);
