CREATE TABLE project
(
    id                      BIGSERIAL PRIMARY KEY,
    author_telegram_user_id BIGINT,
    github_repository_url   VARCHAR(255),
    programming_language    VARCHAR(255),
    roadmap_project         VARCHAR(255),
    added_timestamp         BIGINT
);