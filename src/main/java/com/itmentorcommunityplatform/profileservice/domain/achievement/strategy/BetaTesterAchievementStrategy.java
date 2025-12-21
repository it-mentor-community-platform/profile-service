package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.BETA_TESTER)
public class BetaTesterAchievementStrategy implements AchievementCriteriaChecker {
    @Override
    public boolean checkCriteria() {
        return true;
    }
}
