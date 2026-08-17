package com.itmentorcommunityplatform.profileservice.dto.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public record ProfileInsertInternalRequestDto(

        @NotNull(message = "'telegram_user_id' must be provided")
        @JsonProperty("telegram_user_id")
        Long telegramUserId,

        @NotNull(message = "'details' must be provided")
        @Valid
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

        @NotEmpty(message = "'details' must not be empty")
        private final Map<String, String> map = new HashMap<>();

        @JsonAnySetter
        public void add(String key, String value) {
            map.put(key, value);
        }
    }
}
