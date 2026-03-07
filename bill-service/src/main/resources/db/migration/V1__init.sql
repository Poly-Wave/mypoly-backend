-- bill-service initial schema (Flyway)

CREATE SCHEMA IF NOT EXISTS bill_service;
SET search_path TO bill_service;

-- bill_categories
CREATE TABLE bill_categories (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(50)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    display_order INTEGER      NOT NULL DEFAULT 0,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,

    -- 카테고리 아이콘 경로(자동 생성)
    -- ex) bill-categories/DIGITAL.webp
    icon_key      VARCHAR(255) GENERATED ALWAYS AS ('bill-categories/' || code || '.webp') STORED,

    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uk_bill_categories_code UNIQUE (code)
);

CREATE INDEX idx_bill_categories_active_order ON bill_categories (is_active, display_order);

-- user_bill_interests (유저별 관심 카테고리)
CREATE TABLE user_bill_interests (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    category_id BIGINT      NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_user_bill_interests_category
        FOREIGN KEY (category_id) REFERENCES bill_categories(id) ON DELETE CASCADE,

    CONSTRAINT uk_user_bill_interest_user_category
        UNIQUE (user_id, category_id)
);

CREATE INDEX idx_user_bill_interest_user ON user_bill_interests (user_id);
CREATE INDEX idx_user_bill_interest_category ON user_bill_interests (category_id);