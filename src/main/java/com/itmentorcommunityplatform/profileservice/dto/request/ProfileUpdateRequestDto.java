package com.itmentorcommunityplatform.profileservice.dto.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ProfileUpdateRequestDto {
    private final Map<String, String> details = new HashMap<>();

    @JsonAnySetter
    public void addDetail(String key, String value) {
        details.put(key, value);
    }
}
