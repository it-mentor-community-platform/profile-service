package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.AchievementRepository;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void awardAchievement(ProjectCreatedEvent event, AchievementType achievementType) {
        if (event == null || event.getAuthorTelegramUserId() == null) {
            log.warn("Received empty event or null author ID");
            return;
        }

        Long telegramUserId = event.getAuthorTelegramUserId();

        Optional<Profile> maybeProfile = profileRepository.findByTelegramUserId(telegramUserId);

        if (maybeProfile.isEmpty()) {
            log.warn("Profile not found for telegramUserId {}. Skipping achievement.", telegramUserId);
            return;
        }

        Profile profile = maybeProfile.get();

        if (alreadyHasAchievement(profile.getId(), achievementType)) {
            log.info("Achievement issuance skipped: user (profileId: {}({})) already owns achievement of type: {}",
                    profile.getId(),event.getAuthorTelegramUserId(), achievementType);
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
    }

    private boolean alreadyHasAchievement(Long profileId, AchievementType type) {
        return achievementRepository.existsByProfileIdAndAchievementType(profileId, type);
    }
}