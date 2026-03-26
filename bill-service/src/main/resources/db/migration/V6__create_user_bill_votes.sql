SET search_path TO bill_service;

-- 사용자별 의안 투표 저장 테이블
CREATE TABLE user_bill_votes (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    bill_id     BIGINT      NOT NULL,
    vote_result VARCHAR(50) NOT NULL,
    voted_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_user_bill_votes_bill
        FOREIGN KEY (bill_id) REFERENCES assembly_bills(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_bill_votes_user_bill UNIQUE (user_id, bill_id)
);

CREATE INDEX idx_user_bill_votes_user ON user_bill_votes (user_id);
CREATE INDEX idx_user_bill_votes_bill ON user_bill_votes (bill_id);
CREATE INDEX idx_user_bill_votes_voted_at ON user_bill_votes (voted_at DESC);

COMMENT ON TABLE user_bill_votes IS '사용자별 의안 투표 이력';
COMMENT ON COLUMN user_bill_votes.user_id IS 'user-service 사용자 ID (FK 없이 ID만 보관)';
COMMENT ON COLUMN user_bill_votes.bill_id IS '의안 ID';
COMMENT ON COLUMN user_bill_votes.vote_result IS '사용자 투표 결과 (AGREE/DISAGREE 등)';
COMMENT ON COLUMN user_bill_votes.voted_at IS '사용자가 투표한 시각';
