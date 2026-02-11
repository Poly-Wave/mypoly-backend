-- user-service initial schema (Flyway)

CREATE SCHEMA IF NOT EXISTS user_service;
SET search_path TO user_service;

-- users
CREATE TABLE users (
    id                 BIGSERIAL PRIMARY KEY,
    nickname           VARCHAR(20) UNIQUE,

    gender             VARCHAR(10),
    birth_date         VARCHAR(8),
    profile_image_url  VARCHAR(500),

    sido               VARCHAR(20),
    sigungu            VARCHAR(20),
    emd_name           VARCHAR(20),
    address            VARCHAR(100),

    onboarding_status  VARCHAR(10),

    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- terms (title/content 포함 + version int)
CREATE TABLE terms (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(50)  NOT NULL,
    title          VARCHAR(100) NOT NULL,
    content        TEXT         NOT NULL,
    version        INTEGER      NOT NULL,
    required       BOOLEAN      NOT NULL,
    effective_from DATE         NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_terms_name_version UNIQUE (name, version)
);

CREATE INDEX idx_terms_name ON terms (name);
CREATE INDEX idx_terms_name_version ON terms (name, version);

-- user_oauths
CREATE TABLE user_oauths (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    provider          VARCHAR(10) NOT NULL,
    provider_user_id  VARCHAR(100) NOT NULL,

    CONSTRAINT fk_user_oauths_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT uk_user_oauths_provider_user
        UNIQUE (provider, provider_user_id)
);

CREATE INDEX idx_user_oauths_user_id ON user_oauths (user_id);

-- user_terms (중복 방지 유니크 포함)
CREATE TABLE user_terms (
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT NOT NULL,
    terms_id  BIGINT NOT NULL,
    agreed    BOOLEAN NOT NULL,
    agreed_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_user_terms_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_user_terms_terms
        FOREIGN KEY (terms_id) REFERENCES terms(id) ON DELETE CASCADE,

    CONSTRAINT uk_user_terms_user_terms
        UNIQUE (user_id, terms_id)
);

CREATE INDEX idx_user_terms_user_id ON user_terms (user_id);
CREATE INDEX idx_user_terms_terms_id ON user_terms (terms_id);

-- nickname_forbidden_words
CREATE TABLE nickname_forbidden_words (
    id      BIGSERIAL PRIMARY KEY,
    word    VARCHAR(255) NOT NULL,
    active  BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_nickname_forbidden_words_word ON nickname_forbidden_words (word);

-- nickname_words
CREATE TABLE nickname_words (
    id      BIGSERIAL PRIMARY KEY,
    word    VARCHAR(255) NOT NULL,
    type    VARCHAR(255) NOT NULL,
    active  BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_nickname_words_type ON nickname_words (type);

CREATE TABLE administrative_area (
    id BIGSERIAL PRIMARY KEY,
    sido VARCHAR(20) NOT NULL,
    sigungu VARCHAR(20),
    emd_name VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_admin_area_sido ON administrative_area(sido);
CREATE INDEX idx_admin_area_sigungu ON administrative_area(sigungu);
CREATE INDEX idx_admin_area_emd ON administrative_area(emd_name);
CREATE INDEX idx_admin_area_full ON administrative_area(sido, sigungu, emd_name);
