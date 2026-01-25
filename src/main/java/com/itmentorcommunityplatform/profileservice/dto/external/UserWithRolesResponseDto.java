package com.itmentorcommunityplatform.profileservice.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UserWithRolesResponseDto(
        @JsonProperty("telegram_user_id")
        Long telegramUserId,
        List<String> roles) {
}
