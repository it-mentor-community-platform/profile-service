package com.itmentorcommunityplatform.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;

public record ProjectDto(

        Long id,

        @JsonProperty("author_telegram_user_id")
        Long authorTelegramUserId,

        @JsonProperty("github_repository_url")
        String githubRepositoryUrl,

        @JsonProperty("programming_language")
        String programmingLanguage,

        @JsonProperty("roadmap_project")
        RoadmapProjectType roadmapProject,

        @JsonProperty("added_timestamp")
        Long addedTimestamp

) {
}
