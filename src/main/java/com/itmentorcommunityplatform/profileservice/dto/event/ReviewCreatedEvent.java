package com.itmentorcommunityplatform.profileservice.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmentorcommunityplatform.profileservice.dto.ProjectDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreatedEvent {

    @JsonProperty("id")
    Long id;
    @JsonProperty("reviewer_telegram_user_id")
    String reviewerTelegramUserId;
    @JsonProperty("reviewer_telegram_profile_url")
    String reviewerTelegramProfileUrl;
    @JsonProperty("url")
    String url;
    @JsonProperty("added_timestamp")
    Long addedTimestamp;

    @JsonProperty("project")
    ProjectDto project;

}
