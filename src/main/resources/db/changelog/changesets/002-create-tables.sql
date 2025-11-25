CREATE TABLE profile_schema.profiles (
    id BIGSERIAL PRIMARY KEY,
    telegram_user_id BIGINT NOT NULL
);

CREATE TABLE profile_schema.profiles_details (
    profile_id BIGINT NOT NULL,
    detail_name VARCHAR(255) NOT NULL,
    detail_value VARCHAR(255) NOT NULL,
    CONSTRAINT fk_profiles_details_profiles
        FOREIGN KEY (profile_id)
            REFERENCES profile_schema.profiles(id)
            ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_profiles_details_unique
    ON profile_schema.profiles_details (profile_id, detail_name, detail_value);