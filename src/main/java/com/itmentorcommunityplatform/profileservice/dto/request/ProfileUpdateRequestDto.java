package com.itmentorcommunityplatform.profileservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileUpdateRequestDto(
        @JsonProperty("github_profile_url")
        String githubProfileUrl,
        @JsonProperty("telegram_url")
        String telegramUrl
) {
}
