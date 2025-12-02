package com.itmentorcommunityplatform.profileservice.dto.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public record ProfileUpsertInternalRequestDto(

        @JsonProperty("telegram_user_id")
        Long telegramUserId,

        @JsonProperty("details")
        Details details
) {
    @Getter
    public static class Details {
        private final Map<String, String> map = new HashMap<>();

        @JsonAnySetter
        public void add(String key, String value) {
            map.put(key, value);
        }
    }
}
