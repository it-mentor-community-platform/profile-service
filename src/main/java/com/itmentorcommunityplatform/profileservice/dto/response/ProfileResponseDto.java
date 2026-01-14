package com.itmentorcommunityplatform.profileservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileResponseDto {

    @JsonProperty("telegram_user_id")
    private final Long telegramUserId;

    private final ProfileDetailsResponseDto details;

}
