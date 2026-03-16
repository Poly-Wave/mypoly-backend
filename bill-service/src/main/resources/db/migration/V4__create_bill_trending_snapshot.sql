SET search_path TO bill_service;

-- 요즘 핫한 탭용: 7일 내 투표 완료 수 배치 선계산 결과
CREATE TABLE bill_trending_snapshot (
    bill_id          BIGINT NOT NULL,
    vote_count_7d    INTEGER NOT NULL,
    calculated_at    TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_bill_trending_snapshot PRIMARY KEY (bill_id),
    CONSTRAINT fk_bill_trending_snapshot_bill FOREIGN KEY (bill_id) REFERENCES assembly_bills(id) ON DELETE CASCADE
);

CREATE INDEX idx_bill_trending_snapshot_vote_count_7d ON bill_trending_snapshot (vote_count_7d DESC);

COMMENT ON TABLE bill_trending_snapshot IS '7일 내 투표 완료 수 배치 집계 결과 (요즘 핫한 탭용)';
COMMENT ON COLUMN bill_trending_snapshot.vote_count_7d IS '최근 7일간 해당 의안에 대한 투표 완료 건수';
COMMENT ON COLUMN bill_trending_snapshot.calculated_at IS '집계 시점';
