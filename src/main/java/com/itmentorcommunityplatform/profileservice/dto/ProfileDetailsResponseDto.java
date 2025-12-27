package com.itmentorcommunityplatform.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(
        description = "User Profile Details - an arbitrary set of allowed fields",
        additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
        example = """
        {
          "github_profile_url": "https://github.com/johndoe",
          "telegram_url": "https://t.me/johndoe",
          "twitter_handle": "@johndoe",
          "full_name": "John Doe"
        }
        """
)
public class ProfileDetailsResponseDto {

    private final Map<String, Object> details;

    public ProfileDetailsResponseDto(Map<String, Object> details) {
        this.details = details;
    }

    @JsonAnyGetter
    public Map<String, Object> anyToJson() {
        return details;
    }
}