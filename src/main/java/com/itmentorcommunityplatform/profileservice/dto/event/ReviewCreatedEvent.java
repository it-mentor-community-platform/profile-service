package com.itmentorcommunityplatform.profileservice.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmentorcommunityplatform.profileservice.dto.ProjectDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreatedEvent {

    @NotNull
    @Positive
    @JsonProperty("id")
    Long id;

    @NotNull
    @Positive
    @JsonProperty("reviewer_telegram_user_id")
    Long reviewerTelegramUserId;

    @JsonProperty("reviewer_telegram_profile_url")
    String reviewerTelegramProfileUrl;

    @NotBlank
    @JsonProperty("url")
    String url;

    @NotNull
    @Positive
    @JsonProperty("added_timestamp")
    Long addedTimestamp;

    @NotNull
    @Valid
    @JsonProperty("project")
    ProjectDto project;

}
