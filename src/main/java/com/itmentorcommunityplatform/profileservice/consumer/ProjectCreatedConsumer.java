package com.itmentorcommunityplatform.profileservice.consumer;

import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectCreatedConsumer {

    private final ProfileService profileService;

    @KafkaListener(topics = "projects.project.created", groupId = "profile-service-cg")
    public void consumeProjectCreatedEvent(@Payload ProjectCreatedEvent projectCreatedEvent) {
        log.info("author_telegram_user_id: {}," +
                        "author_telegram_profile_url: {}," +
                        " github_repository_url: {}," +
                        "programming_language: {}," +
                        "roadmap_project: {}," +
                        "added_timestamp: {}," +
                        "projeсt_source_type: {}", projectCreatedEvent.getAuthorTelegramUserId(),
                projectCreatedEvent.getAuthorTelegramProfileUrl(), projectCreatedEvent.getGithubRepositoryUrl(),
                projectCreatedEvent.getProgrammingLanguage(), projectCreatedEvent.getRoadmapProject(),
                projectCreatedEvent.getAddedTimestamp(), projectCreatedEvent.getProjectSourceType());

            profileService.createdProject(projectCreatedEvent);
    }
}
