CREATE TABLE achievements (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    achievement_type VARCHAR(255) NOT NULL,
    earned_timestamp BIGINT NOT NULL,
    publicly_visible BOOLEAN NOT NULL,

    CONSTRAINT fk_achievements_profile
        FOREIGN KEY (profile_id) REFERENCES profiles(id)
);

CREATE UNIQUE INDEX idx_achievements_unique
    ON achievements (profile_id, achievement_type);