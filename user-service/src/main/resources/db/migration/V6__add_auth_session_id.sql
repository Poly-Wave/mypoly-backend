ALTER TABLE user_service.users
    ADD COLUMN IF NOT EXISTS auth_session_id VARCHAR(64);