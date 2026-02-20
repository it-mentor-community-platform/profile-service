package com.itmentorcommunityplatform.profileservice.consumer;

import com.itmentorcommunityplatform.profileservice.dto.event.UserAuthenticatedEvent;
import com.itmentorcommunityplatform.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthUserAuthenticatedConsumer {

    private final ProfileService profileService;

    @KafkaListener(topics = "auth.user.authenticated", groupId = "profile-service-cg")
    public void consumeUserAuthenticatedEvent(UserAuthenticatedEvent event) {
        log.info("Kafka Consumer: Received user authenticated event: {}", event);

    }
}
