package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.config.AchievementConfig;
import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategyRegistry;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.dto.AchievementDto;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.dto.request.AchievementsVisibleRequestDto;
import com.itmentorcommunityplatform.profileservice.repository.AchievementRepository;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final AchievementStrategyRegistry registry;
    private final ProfileService profileService;
    private final TransactionTemplate transactionTemplate;
    private final ProfileRepository profileRepository;
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

        Map<AchievementType, String> achievementsDescriptions = achievementConfig.getAchievements();

        Long profileId = profileRepository.findByTelegramUserId(telegramUserId)
                .map(Profile::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        Map<AchievementType, AchievementDto> profileAchievements = achievementRepository.findAchievemetsByProfileId(profileId)
                .stream()
                .map(achievement -> new AchievementDto(
                        achievement.getAchievementType(),
                        achievement.getEarnedTimestamp(),
                        achievementsDescriptions.get(achievement.getAchievementType()),
                        achievement.isPubliclyVisible()
                ))
                .collect(Collectors.toMap(AchievementDto::getType, Function.identity()));

        achievementsDescriptions.forEach((type, name) -> {
            AchievementDto achievementDto = new AchievementDto(type, 0L, name, true);
            profileAchievements.putIfAbsent(type, achievementDto);
        });

        return new ArrayList<>(profileAchievements.values());
    }

    @Transactional
    public AchievementDto setAchievementPublicity(Long telegramUserId,
                                                  AchievementsVisibleRequestDto visibility,
                                                  String type) {

        Profile profile = profileRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> {
                    log.warn("Profile not found for telegramUserId: {}", telegramUserId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Profile with Telegram-User-Id %s does not exist".formatted(telegramUserId));
                });

        Long profileId = profile.getId();
        AchievementType achievementType = parseAchievementType(type);

        if (!isValidAchievementType(achievementType)) {
            log.error("Unknown achievement type: {}", achievementType);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown achievement type: %s"
                    .formatted(achievementType));
        }

        if (!alreadyHasAchievement(profileId, achievementType)) {
            log.warn("User {} tried to edit achievement {} which they don't own", telegramUserId, type);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User %d does not own the achievement they are trying to edit".formatted(telegramUserId));
        }

        Achievement achievement = achievementRepository.findByProfileIdAndAchievementType(
                profileId,
                achievementType);

        achievement.setPubliclyVisible(visibility.publiclyVisible());

        Achievement savedAchievement = achievementRepository.save(achievement);

        String description = achievementConfig.getAchievements().get(achievementType);

        return new AchievementDto(
                savedAchievement.getAchievementType(),
                savedAchievement.getEarnedTimestamp(),
                description,
                savedAchievement.isPubliclyVisible()
        );
    }

    private AchievementType parseAchievementType(String type) {
        try {
            return AchievementType.valueOf(type.toUpperCase().trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Failed to parse achievement type: {}", type);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid achievement type: " + type);
        }
    }

    private boolean isValidAchievementType(AchievementType achievementType) {
        AchievementType[] values = AchievementType.values();
        for (AchievementType type : values) {
            if (type == achievementType) {
                return true;
            }
        }
        return false;
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

    private boolean alreadyHasAchievement(Long profileId, AchievementType achievementType) {
        if (achievementRepository.existsByProfileIdAndAchievementType(profileId, achievementType)) {
            log.info("User (profileId: {}) already owns achievement of type: {}",
                    profileId, achievementType);
            return true;
        }
        return false;
    }
}