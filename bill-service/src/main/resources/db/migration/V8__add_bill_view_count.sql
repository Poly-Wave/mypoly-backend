SET search_path TO bill_service;

ALTER TABLE bills
    ADD COLUMN view_count BIGINT NOT NULL DEFAULT 0;
