CREATE UNIQUE INDEX idx_profile_details_github_profile_url_unique
    ON profiles_details (detail_value)
    WHERE detail_name = 'github_profile_url';

CREATE UNIQUE INDEX idx_profile_details_telegram_url_unique
    ON profiles_details (detail_value)
    WHERE detail_name = 'telegram_url';