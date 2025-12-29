package com.itmentorcommunityplatform.profileservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AchievementsVisibleRequestDto(
        @JsonProperty(value = "publicly_visible", required = true)
        boolean publiclyVisible
) {
}
