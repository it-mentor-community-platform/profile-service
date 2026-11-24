package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.COMMUNITY_PILAR)
public class CommunityPilarAchievementStrategy implements AchievementCriteriaChecker {
    private final ReviewRepository reviewRepository;

    @Override
    public boolean checkCriteria(Long userId) {
        return reviewRepository.count() >= 100;
    }
}

