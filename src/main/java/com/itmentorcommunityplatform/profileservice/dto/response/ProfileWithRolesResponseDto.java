package com.itmentorcommunityplatform.profileservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmentorcommunityplatform.profileservice.domain.type.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProfileWithRolesResponseDto {
    @JsonProperty("telegram_user_id")
    private final Long telegramUserId;

    private final ProfileDetailsResponseDto details;

    private final List<Role> roles;
}

