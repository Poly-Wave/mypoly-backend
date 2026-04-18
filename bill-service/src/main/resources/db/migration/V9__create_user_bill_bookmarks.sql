SET search_path TO bill_service;

CREATE TABLE user_bill_bookmarks (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    bill_id     BIGINT      NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_user_bill_bookmarks_bill
        FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,

    CONSTRAINT uk_user_bill_bookmarks_user_bill
        UNIQUE (user_id, bill_id)
);

CREATE INDEX idx_user_bill_bookmarks_user_created
    ON user_bill_bookmarks (user_id, created_at DESC);

CREATE INDEX idx_user_bill_bookmarks_bill
    ON user_bill_bookmarks (bill_id);