package com.itmentorcommunityplatform.profileservice.consumer;

import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectCreatedConsumer {

    private final AchievementService achievementService;

    @KafkaListener(topics = "projects.project.created", groupId = "profile-service-cg")
    public void consumeProjectCreatedEvent(ProjectCreatedEvent event) {
        log.info("Added new project: {}", event);
        try {
            achievementService.recheckAndAwardAchievements(event);
            log.info("Kafka Consumer: Successfully processed event for user {}", event.getAuthorTelegramUserId());
        } catch (Exception e) {
            log.error("Kafka Consumer: Error processing event for user: {}. Error: {}",
                    event.getAuthorTelegramUserId(), e.getMessage(), e);
        }
    }
}
