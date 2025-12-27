package com.itmentorcommunityplatform.profileservice.dto;

import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;

public record AchievementsDto(AchievementType type,
                              Long earned_timestamp,
                              String description,
                              boolean publicly_visible)
{}
