package com.itmentorcommunityplatform.profileservice.dto.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public record ProfileInsertInternalRequestDto(

        @JsonProperty("telegram_user_id")
        Long telegramUserId,

        @JsonProperty("details")
        Details details
) {
    @Schema(
            description = "Custom profile Details",
            additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
            example = """
        {
          "github_profile_url": "https://github.com/johndoe",
          "twitter_handle": "@johndoe",
          "portfolio": "https://johndoe.dev"
        }
        """
    )
    @Getter
    public static class Details {
        private final Map<String, String> map = new HashMap<>();

        @JsonAnySetter
        public void add(String key, String value) {
            map.put(key, value);
        }
    }
}
