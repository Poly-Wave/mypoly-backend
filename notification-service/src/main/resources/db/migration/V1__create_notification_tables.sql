-- notification-service initial schema (Flyway)
--
-- 주의:
-- - users 테이블은 user_service schema 에 있으나, 마이크로서비스 정합성을 위해
--   user_notifications.user_id 에 FK 를 박지 않는다. 정합성은 application 레이어에서 보장.

CREATE SCHEMA IF NOT EXISTS notification_service;
SET search_path TO notification_service;

-- notification_policies: 관리자/내부 알림 정책 메타
CREATE TABLE notification_policies (
    id               BIGSERIAL PRIMARY KEY,
    policy_key       VARCHAR(100),
    name             VARCHAR(200) NOT NULL,
    depth            VARCHAR(50),
    channel          VARCHAR(20)  NOT NULL,
    category         VARCHAR(30)  NOT NULL,
    target_audience  VARCHAR(500),
    send_schedule    VARCHAR(500),
    title            VARCHAR(200),
    body             VARCHAR(1000) NOT NULL,
    landing_type     VARCHAR(30)  NOT NULL,
    landing_url      VARCHAR(1000),
    status           VARCHAR(20)  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_notification_policies_policy_key UNIQUE (policy_key)
);

CREATE INDEX idx_notification_policies_status
    ON notification_policies (status);

CREATE INDEX idx_notification_policies_category
    ON notification_policies (category);

-- user_notifications: 사용자 알림 이력(앱 알림함에 노출되는 단위)
CREATE TABLE user_notifications (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT      NOT NULL,
    policy_id     BIGINT,
    channel       VARCHAR(20) NOT NULL,
    category      VARCHAR(30) NOT NULL,
    title         VARCHAR(200),
    body          VARCHAR(1000) NOT NULL,
    landing_type  VARCHAR(30) NOT NULL,
    landing_id    BIGINT,
    landing_url   VARCHAR(1000),
    dedup_key     VARCHAR(200),
    sent_at       TIMESTAMPTZ NOT NULL,
    read_at       TIMESTAMPTZ,
    deleted_at    TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_user_notifications_policy
        FOREIGN KEY (policy_id) REFERENCES notification_policies(id) ON DELETE SET NULL,

    CONSTRAINT uk_user_notifications_user_dedup
        UNIQUE (user_id, dedup_key)
);

CREATE INDEX idx_user_notifications_user_created
    ON user_notifications (user_id, created_at DESC);

CREATE INDEX idx_user_notifications_user_unread
    ON user_notifications (user_id, read_at);
