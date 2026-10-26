package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Profile;

public interface ProfileJdbcTemplateRepository<T> {
    <S extends T> S save(S profile);
}
