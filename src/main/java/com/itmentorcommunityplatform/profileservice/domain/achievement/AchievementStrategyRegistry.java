package com.itmentorcommunityplatform.profileservice.domain.achievement;

import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AchievementStrategyRegistry {

    Map<AchievementType, AchievementCriteriaChecker> strategies = new HashMap<>();

    public AchievementStrategyRegistry(List<AchievementCriteriaChecker> allStrategy) {
        for (AchievementCriteriaChecker checker : allStrategy) {
            AchievementStrategy annotation =
                    checker.getClass().getAnnotation(AchievementStrategy.class);

            if (annotation != null) {
                strategies.put(annotation.type(), checker);
            }
        }
    }

    public AchievementCriteriaChecker get(AchievementType achievementType) {
        return strategies.get(achievementType);
    }

}