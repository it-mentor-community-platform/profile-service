package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.dto.AchievementDto;
import com.itmentorcommunityplatform.profileservice.dto.event.UserAuthenticatedEvent;
import com.itmentorcommunityplatform.profileservice.dto.event.UserCreatedEvent;
import com.itmentorcommunityplatform.profileservice.exception.ProfileNotFoundException;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfilePersistenceService profilePersistenceService;

    public void createOrUpdateProfile(UserCreatedEvent event) {
        if (event == null || event.getTelegramUserId() == null) {
            log.warn("Received empty event or null telegramUserId. Skipping.");
            return;
        }

        Long telegramUserId = event.getTelegramUserId();
        log.info("Attempting to create or update profile for telegramUserId {}", telegramUserId);

        Map<String, String> details = new HashMap<>();

        if (event.getFirstName() != null && !event.getFirstName().isBlank()) {
            details.put(ProfileDetailType.FIRST_NAME.getDetailName(), event.getFirstName());
        }
        if (event.getLastName() != null && !event.getLastName().isBlank()) {
            details.put(ProfileDetailType.LAST_NAME.getDetailName(), event.getLastName());
        }

        Optional<Profile> maybeProfile = profileRepository.findByTelegramUserId(telegramUserId);

        if (maybeProfile.isPresent()) {
            log.info("Profile for telegramUserId: {} exists. Updating details.", telegramUserId);

            profilePersistenceService.updateProfile(telegramUserId, details);
        } else {
            log.info("Creating new profile for telegramUserId: {}", telegramUserId);

            Profile profile = profilePersistenceService.createProfile(telegramUserId, details);

            log.info("Successfully created profile with telegramUserId: {}", profile.getTelegramUserId());
        }
    }

    public void upsertProfile(UserAuthenticatedEvent event) {
        if (event == null || event.getTelegramUserId() == null) {
            log.warn("Received empty event or null telegramUserId. Skipping.");
            return;
        }

        Long telegramUserId = event.getTelegramUserId();
        log.info("Attempting to update profile for telegramUserId: {}", telegramUserId);

        Optional<Profile> existingProfile = profileRepository.findByTelegramUserId(telegramUserId);
        if (existingProfile.isEmpty()) {
            log.warn("Profile for telegramUserId: {} not found. Skipping.", telegramUserId);
            return;
        }

        Map<String, String> newDetails = new HashMap<>();

        if (event.getTelegramUsername() != null && !event.getTelegramUsername().isBlank()) {
            newDetails.put(ProfileDetailType.TELEGRAM_URL.getDetailName(), "https://t.me/" + event.getTelegramUsername());
        }
        if (event.getFirstName() != null && !event.getFirstName().isBlank()) {
            newDetails.put(ProfileDetailType.FIRST_NAME.getDetailName(), event.getFirstName());
        }
        if (event.getLastName() != null && !event.getLastName().isBlank()) {
            newDetails.put(ProfileDetailType.LAST_NAME.getDetailName(), event.getLastName());
        }

        if (newDetails.isEmpty()) {
            log.info("No details provided. Nothing to update for telegramUserId: {}", telegramUserId);
            return;
        }

        profilePersistenceService.updateProfile(telegramUserId, newDetails);

        log.info("Profile details processed for telegramUserId: {}", telegramUserId);
    }

    @Transactional(readOnly = true)
    public Optional<Profile> getProfileForEvent(AchievementDto event) {
        if (event == null || event.telegramUserId() == null) {
            log.warn("Received empty event or null author ID");
            return Optional.empty();
        }

        Long telegramUserId = event.telegramUserId();

        Optional<Profile> maybeProfile = profileRepository.findByTelegramUserId(telegramUserId);

        if (maybeProfile.isEmpty()) {
            log.warn("Profile not found for telegramUserId {}", telegramUserId);
            return Optional.empty();
        }

        return maybeProfile;
    }

    @Transactional(readOnly = true)
    public Profile getProfileByTelegramIdOrThrow(Long telegramUserId) {
        return profileRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> {
                    log.warn("Profile not found for telegramUserId: {}", telegramUserId);
                    return new ProfileNotFoundException("Profile not found");
                });
    }
}