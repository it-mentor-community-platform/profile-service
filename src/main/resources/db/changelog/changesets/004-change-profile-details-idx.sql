DROP INDEX idx_profiles_details_unique;

CREATE UNIQUE INDEX idx_profiles_details_unique
    ON profiles_details (profile_id, detail_name);