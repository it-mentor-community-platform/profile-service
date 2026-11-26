package com.itmentorcommunityplatform.profileservice.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ProjectUpdatedEvent {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("author_telegram_user_id")
    private Long authorTelegramUserId;

    @JsonProperty("github_repository_url")
    private String githubRepositoryUrl;

    @JsonProperty("previous_github_repository_url")
    private String previousGithubRepositoryUrl;

    @JsonProperty("programming_language")
    private String programmingLanguage;

    @JsonProperty("roadmap_project")
    private String roadmapProject;

    @JsonProperty("added_timestamp")
    private Long addedTimestamp;

    @JsonProperty("deployment_url")
    private String deploymentUrl;
}