package com.itmentorcommunityplatform.profileservice.consumer;

import com.itmentorcommunityplatform.profileservice.dto.event.ProjectUpdatedEvent;
import com.itmentorcommunityplatform.profileservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectUpdatedConsumer {

    private final ProjectService projectService;

    @KafkaListener(
            topics = "${spring.kafka.topic.projects-project-updated}",
            groupId = "profile-service-cg",
            containerFactory = "multiTypeKafkaListenerContainerFactory"
    )
    public void consumeProjectUpdatedEvent(ProjectUpdatedEvent event) {
        log.info("ProjectUpdated received: userId={}, previousUrl={}, newUrl={}",
                event.getAuthorTelegramUserId(),
                event.getPreviousGithubRepositoryUrl(),
                event.getGithubRepositoryUrl());

        projectService.updateProject(event);

        log.info("ProjectUpdated processed: userId={}",
                event.getAuthorTelegramUserId());
    }
}