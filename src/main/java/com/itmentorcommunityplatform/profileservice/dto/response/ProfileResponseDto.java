package com.itmentorcommunityplatform.profileservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDto {

    @JsonProperty("telegram_user_id")
    private final Long telegramUserId;

    private final ProfileDetailsResponseDto details;

    private final List<ProfileAchievementsResponseDto> achievements;
}
