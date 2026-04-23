CREATE INDEX IF NOT EXISTS idx_user_bill_votes_user_voted_at
    ON user_bill_votes (user_id, voted_at DESC);

CREATE INDEX IF NOT EXISTS idx_user_bill_votes_user_vote_result_voted_at
    ON user_bill_votes (user_id, vote_result, voted_at DESC);