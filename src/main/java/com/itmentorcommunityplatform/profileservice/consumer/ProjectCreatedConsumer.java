package com.itmentorcommunityplatform.profileservice.consumer;

import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectCreatedConsumer {

    @KafkaListener(topics = "projects.project.created", groupId = "profile-service-cg")
    public void consumeProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {
        log.info("Added new project: {}", projectCreatedEvent);
    }
}
