package com.itmentorcommunityplatform.profileservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreatedEvent {
    Long authorTelegramUserId;
    String authorTelegramProfileUrl;
    String githubRepositoryUrl;
    String programmingLanguage;
    String roadmapProject;
    Long addedTimestamp;
    String projectSourceType;
}
