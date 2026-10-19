package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Profile;

public interface ProfileRepositoryCustom {

    void createProfile(Profile profile);
    void updateProfile(Profile profile, Long id);
}
