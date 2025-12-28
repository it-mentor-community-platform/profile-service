package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.config.AchievementConfig;
import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategyRegistry;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.dto.AchievementDto;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.AchievementRepository;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final ProfileRepository profileRepository;
    private final AchievementStrategyRegistry registry;
    private final ProfileService profileService;
    private final TransactionTemplate transactionTemplate;
    private final AchievementConfig achievementConfig;

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

    public List<AchievementDto> getProfileAchievements(Long telegramUserId) {

        List<AchievementDto> listAchievementsProfile = new ArrayList<>();

        Profile profile = profileRepository.findByTelegramUserId(telegramUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        List<Achievement> achievements = achievementRepository.findAchievemetsByProfileId(profile.getId());

        for (Map.Entry<AchievementType, String> entry : achievementConfig.getAchievements().entrySet()) {
            AchievementType type = entry.getKey();
            String achievementName = entry.getValue();
            boolean isfound = false;

            for (Achievement achievement : achievements) {
                if (achievement.getAchievementType() == type) {
                    listAchievementsProfile.add(new AchievementDto(type, achievement.getEarnedTimestamp(), achievementName, achievement.isPubliclyVisible()));
                    isfound = true;
                    break;
                }
            }

            if (!isfound) {
                listAchievementsProfile.add(new AchievementDto(type, 0L, achievementName, true));
            }

        }

        return listAchievementsProfile;
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