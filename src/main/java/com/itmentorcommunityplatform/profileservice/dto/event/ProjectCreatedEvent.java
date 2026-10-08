package com.itmentorcommunityplatform.profileservice.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreatedEvent implements AchievementEvent {
    @JsonProperty("author_telegram_user_id")
    Long authorTelegramUserId;
    @JsonProperty("author_telegram_profile_url")
    String authorTelegramProfileUrl;
    @JsonProperty("github_repository_url")
    String githubRepositoryUrl;
    @JsonProperty("programming_language")
    String programmingLanguage;
    @JsonProperty("roadmap_project")
    String roadmapProject;
    @JsonProperty("added_timestamp")
    Long addedTimestamp;
    @JsonProperty("project_source_type")
    String projectSourceType;

    @Override
    public Long getTelegramUserId() {
        return this.authorTelegramUserId;
    }
}
