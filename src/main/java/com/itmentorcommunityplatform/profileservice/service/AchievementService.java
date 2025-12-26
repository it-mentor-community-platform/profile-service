package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategyRegistry;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final AchievementStrategyRegistry registry;
    private final ProfileService profileService;
    private final TransactionTemplate transactionTemplate;

    public void recheckAndAwardAchievements(ProjectCreatedEvent event) {
        profileService.getProfileForEvent(event).ifPresent(profile ->
                transactionTemplate.executeWithoutResult(status -> {
                    for (AchievementType type : AchievementType.values()) {
                        AchievementCriteriaChecker checker = registry.getStrategy(type);
                        if (checker != null && checker.checkCriteria(event)) {
                            awardAchievement(event, type, profile);
                        }
                    }
                }));
    }

    private void awardAchievement(ProjectCreatedEvent event, AchievementType achievementType, Profile profile) {
        if (alreadyHasAchievement(profile.getId(), achievementType)) {
            log.info("Achievement issuance skipped: user (profileId: {}({})) already owns achievement of type: {}",
                    profile.getId(), event.getAuthorTelegramUserId(), achievementType);
            return;
        }

        Achievement achievement = Achievement
                .builder()
                .profileId(profile.getId())
                .achievementType(achievementType)
                .earnedTimestamp(System.currentTimeMillis())
                .publiclyVisible(true)
                .build();

        achievementRepository.save(achievement);
        log.info("User (profileId: {}({})), earned achievement: {}",
                profile.getId(), event.getAuthorTelegramUserId(), achievementType);

    }

    private boolean alreadyHasAchievement(Long profileId, AchievementType type) {
        return achievementRepository.existsByProfileIdAndAchievementType(profileId, type);
    }
}