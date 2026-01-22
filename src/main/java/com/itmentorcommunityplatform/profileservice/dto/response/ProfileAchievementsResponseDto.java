package com.itmentorcommunityplatform.profileservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileAchievementsResponseDto {

    @JsonProperty("type")
    private final AchievementType achievementType;

    @JsonProperty("earned_timestamp")
    private final Long earnedTimestamp;
}
