package com.itmentorcommunityplatform.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class AchievementDto
{
    AchievementType type;

    @JsonProperty("earned_timestamp")
    Long earnedTimestamp;

    String description;

    @JsonProperty("publicly_visible")
    boolean publiclyVisible;
}
