package com.itmentorcommunityplatform.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.Map;

public class ProfileDto {

    private final Map<String, String> details;

    public ProfileDto(Map<String, String> details) {
        this.details = details;
    }

    @JsonAnyGetter
    public Map<String, String> anyToJson() {
        return details;
    }
}