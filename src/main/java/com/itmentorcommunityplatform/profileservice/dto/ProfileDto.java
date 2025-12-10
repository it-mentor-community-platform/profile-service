package com.itmentorcommunityplatform.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileDto {

    @JsonProperty("telegram_user_id")
    private final Long telegramUserId;

    private final ProfileDetailDto details;

}
