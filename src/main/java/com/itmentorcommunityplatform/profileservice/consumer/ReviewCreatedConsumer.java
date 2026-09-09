package com.itmentorcommunityplatform.profileservice.consumer;

import com.itmentorcommunityplatform.profileservice.dto.event.ReviewCreatedEvent;
import com.itmentorcommunityplatform.profileservice.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewCreatedConsumer {

    private final ReviewService reviewService;

    @KafkaListener(
            topics = "${spring.kafka.topic.reviews-review-created}",
            groupId = "profile-service-cg",
            containerFactory = "multiTypeKafkaListenerContainerFactory"
    )
    public void consumeReviewCreatedEvent(@NotNull @Valid ReviewCreatedEvent event) {
        log.info("ReviewCreated received: url={}", event.getUrl());

        try {
            reviewService.save(event);
            log.info("Kafka Consumer: Successfully save review {}", event.getUrl());
        } catch (Exception e) {
            log.error("Kafka Consumer: Error processing event for review: {}",
                    event.getUrl(), e);
        }
    }
}

