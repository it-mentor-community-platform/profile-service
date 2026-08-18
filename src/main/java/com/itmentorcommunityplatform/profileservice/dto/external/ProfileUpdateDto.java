package com.itmentorcommunityplatform.profileservice.dto.external;

import com.itmentorcommunityplatform.profileservice.dto.response.ProfileInsertInternalResponseDto;

public record ProfileUpdateDto(
        ProfileInsertInternalResponseDto profileDto,
        boolean isProfileWasExist
) {
}
