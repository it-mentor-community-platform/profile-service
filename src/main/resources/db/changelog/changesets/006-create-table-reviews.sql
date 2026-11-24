CREATE TABLE reviews
(
    id                        BIGSERIAL PRIMARY KEY,
    project_id                BIGINT NOT NULL,
    reviewer_telegram_user_id BIGINT NOT NULL,
    url                       TEXT   NOT NULL,
    added_timestamp           BIGINT NOT NULL,

    CONSTRAINT fk_reviews_project
        FOREIGN KEY (project_id)
            REFERENCES project (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_reviews_reviewer_telegram_user_id
    ON reviews (reviewer_telegram_user_id);