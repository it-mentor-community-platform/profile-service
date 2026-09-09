package com.itmentorcommunityplatform.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProjectDto(

        @NotNull
        @Positive
        Long id,

        @NotNull
        @JsonProperty("author_telegram_user_id")
        Long authorTelegramUserId,

        @NotBlank
        @JsonProperty("github_repository_url")
        String githubRepositoryUrl,

        @NotBlank
        @JsonProperty("programming_language")
        String programmingLanguage,

        @NotNull

        @JsonProperty("roadmap_project")
        RoadmapProjectType roadmapProject,

        @NotNull
        @Positive
        @JsonProperty("added_timestamp")
        Long addedTimestamp

) {
}
