ALTER TABLE profiles
    ADD CONSTRAINT uk_telegram_user_id
        UNIQUE (telegram_user_id);