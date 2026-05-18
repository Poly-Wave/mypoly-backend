SET search_path TO bill_service;

CREATE TABLE bill_view_week_baseline (
    week_start            DATE NOT NULL,
    bill_id               BIGINT NOT NULL,
    baseline_view_count   BIGINT NOT NULL,

    CONSTRAINT pk_bill_view_week_baseline PRIMARY KEY (week_start, bill_id),
    CONSTRAINT fk_bill_view_week_baseline_bill FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE
);

CREATE INDEX idx_bill_view_week_baseline_week_start ON bill_view_week_baseline (week_start);

COMMENT ON TABLE bill_view_week_baseline IS '주간 인기 안건용: KST 월요일 00:00 기준 주 시작 시점 view_count baseline';
COMMENT ON COLUMN bill_view_week_baseline.week_start IS '해당 주 월요일 날짜 (KST)';
COMMENT ON COLUMN bill_view_week_baseline.baseline_view_count IS '주 시작 시점 bills.view_count';

CREATE TABLE bill_popular_view_ranking (
    week_start          DATE NOT NULL,
    rank                SMALLINT NOT NULL,
    bill_id             BIGINT NOT NULL,
    view_count_weekly   BIGINT NOT NULL,
    calculated_at       TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_bill_popular_view_ranking PRIMARY KEY (week_start, rank),
    CONSTRAINT uq_bill_popular_view_ranking_week_bill UNIQUE (week_start, bill_id),
    CONSTRAINT fk_bill_popular_view_ranking_bill FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    CONSTRAINT chk_bill_popular_view_ranking_rank CHECK (rank BETWEEN 1 AND 5)
);

CREATE INDEX idx_bill_popular_view_ranking_week_start ON bill_popular_view_ranking (week_start);

COMMENT ON TABLE bill_popular_view_ranking IS '주간 조회 증가분 Top 5 배치 결과 (인기 안건 API용)';
COMMENT ON COLUMN bill_popular_view_ranking.view_count_weekly IS '이번 주 조회 증가분 (현재 view_count - baseline)';
