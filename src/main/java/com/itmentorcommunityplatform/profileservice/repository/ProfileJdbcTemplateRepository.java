package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Profile;

public interface ProfileJdbcTemplateRepository {
    Profile save(Profile profile);
}
