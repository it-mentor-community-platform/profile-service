package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.Review;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.COMMUNITY_PILAR)
public class CommunityPilarAchievementStrategy implements AchievementCriteriaChecker {
    private final ReviewRepository reviewRepository;

    @Override
    public boolean checkCriteria(Long userId) {
        List<Review> reviewsByUser = reviewRepository.findByReviewerTelegramUserId(userId);

        return reviewsByUser.size() >= 100;
    }
}

