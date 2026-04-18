SET search_path TO bill_service;

ALTER TABLE user_bill_votes
ADD COLUMN voter_age_band VARCHAR(20);

CREATE INDEX idx_user_bill_votes_age_band ON user_bill_votes (voter_age_band);

COMMENT ON COLUMN user_bill_votes.voter_age_band IS '투표 시점 사용자 연령대 (TEN/TWENTY/THIRTY/FORTY/FIFTY/SIXTY_PLUS)';

DELETE FROM agenda_tabs
WHERE code = 'PERSONALIZED';

INSERT INTO agenda_tabs (code, label, description, display_order, is_active)
VALUES
    ('RECENT_30D', '최근 30일', '최근 30일 등록 안건 중 이번 달 누적 투표 완료 수 순', 3, TRUE),
    ('SAME_AGE', '내 또래', '최근 7일 등록 안건 중 동일 연령대 투표 완료 수 순', 4, TRUE)
ON CONFLICT (code) DO UPDATE
SET label = EXCLUDED.label,
    description = EXCLUDED.description,
    display_order = EXCLUDED.display_order,
    is_active = EXCLUDED.is_active,
    updated_at = now();

UPDATE agenda_tabs
SET display_order = 1
WHERE code = 'HOT_DEBATE';

UPDATE agenda_tabs
SET display_order = 2
WHERE code = 'TRENDING';
