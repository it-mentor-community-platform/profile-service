package com.itmentorcommunityplatform.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {

    @JsonProperty("github_profile_url")
    private String githubProfileUrl;

    @JsonProperty("telegram_url")
    private String telegramUrl;
}