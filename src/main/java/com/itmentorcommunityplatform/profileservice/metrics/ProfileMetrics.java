package com.itmentorcommunityplatform.profileservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ProfileMetrics {

    private final Counter getProfileSuccessCounter;
    private final Counter getProfileErrorCounter;
    private final Timer getProfileTimer;

    public ProfileMetrics(MeterRegistry meterRegistry) {
        this.getProfileSuccessCounter = Counter.builder("profile_service_get_profile_success_total")
                .description("Total successful profile retrieval requests")
                .register(meterRegistry);

        this.getProfileErrorCounter = Counter.builder("profile_service_get_profile_error_total")
                .description("Total failed profile retrieval requests")
                .register(meterRegistry);

        this.getProfileTimer = Timer.builder("profile_service_get_profile_duration_seconds")
                .description("Time taken to retrieve user profile")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(meterRegistry);
    }
}