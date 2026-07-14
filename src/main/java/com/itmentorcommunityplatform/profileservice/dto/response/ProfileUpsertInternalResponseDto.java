package com.itmentorcommunityplatform.profileservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileUpsertInternalResponseDto (
    @JsonProperty("id")
    Long id,

    @JsonProperty("telegram_user_id")
    Long telegramId,

    @JsonProperty("details")
    ProfileDetailsResponseDto details
){}