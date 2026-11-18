package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileWithAchievementsResponseDto;
import com.itmentorcommunityplatform.profileservice.exception.ProfileNotFoundException;
import com.itmentorcommunityplatform.profileservice.mapper.ProfileMapper;
import com.itmentorcommunityplatform.profileservice.metrics.ProfileMetrics;
import com.itmentorcommunityplatform.profileservice.repository.AchievementRepository;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentProfileService {

    private final ProfileMetrics profileMetrics;
    private final ProfileRepository profileRepository;
    private final AchievementRepository achievementRepository;
    private final ProfileMapper profileMapper;
    private final ProfileHelperService profileHelperService;
    private final ProfilePersistenceService profilePersistenceService;

    public ProfileWithAchievementsResponseDto updateCurrentProfile(Long telegramUserId, ProfileUpdateRequestDto dto, String telegramUsername) {
        return profileMetrics.getGetProfileTimer().record(() -> {
            try {
                Map<String, String> newDetailsMap = dto.getDetails();
                profileHelperService.validateDetails(newDetailsMap);

                if (telegramUsername != null && !telegramUsername.isBlank()) {
                    newDetailsMap.put(ProfileDetailType.TELEGRAM_URL.getDetailName(), "https://t.me/" + telegramUsername);
                }

                Profile profile = profilePersistenceService.updateProfile(telegramUserId, newDetailsMap);

                List<Achievement> achievements = achievementRepository
                        .findAllByProfileIdAndPubliclyVisibleTrue(profile.getId());

                profileMetrics.getGetProfileSuccessCounter().increment();

                return profileMapper.mapToProfileWithAchievementsDto(profile.getDetails(), achievements);

            } catch (Exception e) {
                profileMetrics.getGetProfileErrorCounter().increment();
                throw e;
            }
        });
    }

    public ProfileWithAchievementsResponseDto getCurrentUserProfile(Long telegramUserId) {
        return profileMetrics.getGetProfileTimer().record(() -> {
            log.info("Fetching profile for telegramUserId: {}", telegramUserId);
            try {
                Profile profile = getProfileOrThrow(telegramUserId);

                List<Achievement> achievements = achievementRepository.findAllByProfileIdAndPubliclyVisibleTrue(profile.getId());

                profileMetrics.getGetProfileSuccessCounter().increment();

                return profileMapper.mapToProfileWithAchievementsDto(profile.getDetails(), achievements);

            } catch (Exception e) {
                profileMetrics.getGetProfileErrorCounter().increment();
                throw e;
            }
        });
    }

    public ProfileWithAchievementsResponseDto getUserProfile(Long profileId) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> {
                    log.warn("Profile with id: {} not found", profileId);
                    return new ProfileNotFoundException("Profile not found");
                });

        List<Achievement> achievements = achievementRepository.findAllByProfileIdAndPubliclyVisibleTrue(profileId);

        return profileMapper.mapToProfileWithAchievementsDto(profile.getDetails(), achievements);
    }

    private Profile getProfileOrThrow(Long telegramUserId) {
        return profileRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
    }
}
