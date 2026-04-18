SET search_path TO bill_service;

CREATE TABLE user_bill_views (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT      NOT NULL,
    bill_id         BIGINT      NOT NULL,
    last_viewed_at  TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_user_bill_views_bill
        FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,

    CONSTRAINT uk_user_bill_views_user_bill
        UNIQUE (user_id, bill_id)
);

CREATE INDEX idx_user_bill_views_bill
    ON user_bill_views (bill_id);

CREATE INDEX idx_user_bill_views_user_last_viewed
    ON user_bill_views (user_id, last_viewed_at DESC);