package com.itmentorcommunityplatform.profileservice.domain.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ProfileDetailType {

    GITHUB_PROFILE_URL("github_profile_url"),

    TELEGRAM_URL("telegram_url");

    private final String detailName;

    ProfileDetailType(String detailName) {
        this.detailName = detailName;
    }

    public static Optional<ProfileDetailType> fromName(String searchingName) {
        return Arrays.stream(values())
                .filter(type -> type.detailName.equals(searchingName))
                .findFirst();
    }
}
