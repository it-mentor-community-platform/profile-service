package com.itmentorcommunityplatform.profileservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Builder
public class ProfileNoIdResponseDto {

    private final ProfileDetailsResponseDto details;

    private final List<ProfileAchievementsResponseDto> achievements;

}
