SET search_path TO user_service;

-- 온보딩 마일스톤 진입 시각.
-- - 한 번 채워지면 갱신하지 않는 immutable 시각이다.
-- - 별명 변경/관심주제 재선택 등 사용자 행동으로 마일스톤이 "다시 시작" 되지 않게 하여,
--   D+1 형태 리마인더 알림이 반복 발송되는 사고를 방지한다.
-- - 마일스톤이 박히는 시점 (UserCommandServiceImpl.updateUserOnboardingStatus 기준):
--     nickname_set_at      : SIGNUP   진입 시 (회원가입 직후, 닉네임 박힘)
--     category_set_at      : CATEGORY 진입 시 (관심 주제 선택 완료)
--     profile_completed_at : COMPLETE 진입 시 (추가 정보 입력 완료)
--   ONBOARDING 은 마일스톤이 박히지 않는 중간 단계.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS nickname_set_at      TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS category_set_at      TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS profile_completed_at TIMESTAMPTZ;

-- 기존 유저 backfill: onboarding_status 로부터 마일스톤 시각을 추정한다.
-- 정확한 시각은 알 수 없으므로 created_at 으로 대체한다.
-- 회원가입 흐름상 SIGNUP 진입 시점에 닉네임이 박히므로, SIGNUP 이상이면 모두 nickname_set_at 채운다.
UPDATE users SET nickname_set_at      = created_at
 WHERE nickname_set_at IS NULL
   AND onboarding_status IN ('SIGNUP', 'ONBOARDING', 'CATEGORY', 'COMPLETE');

UPDATE users SET category_set_at      = COALESCE(updated_at, created_at)
 WHERE category_set_at IS NULL
   AND onboarding_status IN ('CATEGORY', 'COMPLETE');

UPDATE users SET profile_completed_at = COALESCE(updated_at, created_at)
 WHERE profile_completed_at IS NULL
   AND onboarding_status = 'COMPLETE';

-- 행 1 "별명 설정 D+1 + 관심 주제 미선택" 세그먼트 쿼리용 부분 인덱스.
-- WHERE 절이 좁기 때문에 인덱스가 매우 작고 효율적이다.
CREATE INDEX IF NOT EXISTS idx_users_nickname_remind
    ON users (nickname_set_at)
    WHERE nickname_set_at IS NOT NULL AND category_set_at IS NULL;

-- 행 2 "관심 주제 선택 D+1 + 추가 정보 미입력" 세그먼트 쿼리용 부분 인덱스.
CREATE INDEX IF NOT EXISTS idx_users_category_remind
    ON users (category_set_at)
    WHERE category_set_at IS NOT NULL AND profile_completed_at IS NULL;
