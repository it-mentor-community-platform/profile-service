package com.itmentorcommunityplatform.profileservice.dto.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Schema(
        description = "Profile Update Request - any set of allowed fields",
        additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
        example = """
                {
                  "github_profile_url": "https://github.com/johndoe",
                  "first_name": "Dmitry",
                  "last_name": "OxErr"
                }
                """
)
@Getter
public class ProfileUpdateRequestDto {
    private final Map<String, String> details = new HashMap<>();

    @JsonAnySetter
    public void addDetail(String key, String value) {
        details.put(key, value);
    }
}
