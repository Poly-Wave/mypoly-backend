SET search_path TO bill_service;

ALTER TABLE user_bill_votes
ADD COLUMN voter_gender VARCHAR(20);

CREATE INDEX idx_user_bill_votes_gender ON user_bill_votes (voter_gender);

COMMENT ON COLUMN user_bill_votes.voter_gender IS '투표 시점 사용자 성별 (MAN/WOMAN)';
