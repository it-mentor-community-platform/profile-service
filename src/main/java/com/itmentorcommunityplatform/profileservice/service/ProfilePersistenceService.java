package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.exception.ProfileAlreadyExistException;
import com.itmentorcommunityplatform.profileservice.exception.ProfileNotFoundException;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilePersistenceService {
    private static final String UNIQUE_VIOLATION_SQL_STATE = "23505";
    private static final String GITHUB_URL_INDEX = "idx_profile_details_github_profile_url_unique";
    private static final String TELEGRAM_URL_INDEX = "idx_profile_details_telegram_url_unique";
    private static final String TELEGRAM_USER_ID_CONSTRAINT = "uk_telegram_user_id";

    private final static Set<String> UNIQUE_DETAIL_NAMES = Set.of(
            ProfileDetailType.GITHUB_PROFILE_URL.getDetailName(),
            ProfileDetailType.TELEGRAM_URL.getDetailName()
    );

    private final ProfileRepository profileRepository;

    public Profile createProfile(Long telegramUserId, Map<String, String> details) {
        if (profileRepository.findByTelegramUserId(telegramUserId).isPresent()) {
            log.warn("Profile with TelegramID: {} already exists", telegramUserId);
            throw new ProfileAlreadyExistException("Profile with telegramUserId %d already exists"
                    .formatted(telegramUserId));
        }
        Set<String> skippedDetails = new HashSet<>();
        while (true) {
            Set<String> conflicts = findConflictingDetails(null, details, skippedDetails);
            logSkippedDetails(telegramUserId, conflicts);
            skippedDetails.addAll(conflicts);
            Profile profile = new Profile(telegramUserId, mapToDetails(details, skippedDetails));
            Optional<Profile> savedProfile = trySaveProfile(profile, skippedDetails);
            if (savedProfile.isPresent()) {
                return savedProfile.get();
            }
        }
    }

    public Profile updateProfile(Long telegramUserId, Map<String, String> updatedDetails) {
        Profile profile = getProfileByTelegramUserId(telegramUserId);
        Set<String> skippedDetails = new HashSet<>();
        while (true) {
            Set<String> conflicts = findConflictingDetails(profile.getId(), updatedDetails, skippedDetails);
            logSkippedDetails(telegramUserId, conflicts);
            skippedDetails.addAll(conflicts);
            mergeDetails(profile, updatedDetails, skippedDetails);
            Optional<Profile> savedProfile = trySaveProfile(profile, skippedDetails);
            if (savedProfile.isPresent()) {
                return savedProfile.get();
            }
            profile = getProfileByTelegramUserId(telegramUserId);
        }
    }

    private Optional<Profile> trySaveProfile(Profile profile, Set<String> skippedDetails) {
        try {
            return Optional.of(profileRepository.save(profile));
        } catch (DbActionExecutionException | DataIntegrityViolationException exception) {
            if (isUniqueConstraintViolation(exception, TELEGRAM_USER_ID_CONSTRAINT)) {
                log.warn("Profile with TelegramID: {} already exists", profile.getTelegramUserId());
                throw new ProfileAlreadyExistException("Profile with telegramUserId %d already exists"
                        .formatted(profile.getTelegramUserId()));
            }
            Set<String> racedConflicts = new HashSet<>();
            if (isUniqueConstraintViolation(exception, GITHUB_URL_INDEX)) {
                racedConflicts.add(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName());
            }
            if (isUniqueConstraintViolation(exception, TELEGRAM_URL_INDEX)) {
                racedConflicts.add(ProfileDetailType.TELEGRAM_URL.getDetailName());
            }
            if (racedConflicts.isEmpty()) {
                throw exception;
            }
            logSkippedDetails(profile.getTelegramUserId(), racedConflicts);
            skippedDetails.addAll(racedConflicts);
            return Optional.empty();
        }
    }

    private void mergeDetails(Profile profile, Map<String, String> updatedDetails, Set<String> skippedDetails) {
        Map<String, ProfileDetail> mergedDetails = new LinkedHashMap<>();
        profile.getDetails().forEach(detail -> mergedDetails.put(detail.getDetailName(), detail));
        updatedDetails.forEach((name, value) -> {
            if (!skippedDetails.contains(name)) {
                mergedDetails.put(name, new ProfileDetail(name, value));
            }
        });
        profile.setDetails(new HashSet<>(mergedDetails.values()));
    }

    private Set<String> findConflictingDetails(Long currentProfileId,
                                               Map<String, String> details, Set<String> alreadySkipped) {
        Set<String> conflicts = new HashSet<>();
        details.forEach((name, value) -> {
            if (alreadySkipped.contains(name) || !UNIQUE_DETAIL_NAMES.contains(name)) {
                return;
            }
            Optional<Profile> profile = Optional.empty();
            if (ProfileDetailType.GITHUB_PROFILE_URL.getDetailName().equals(name)) {
                profile = profileRepository.findProfileByGitHubUrl(value);
            }
            if (ProfileDetailType.TELEGRAM_URL.getDetailName().equals(name)) {
                profile = profileRepository.findProfileByTgUrl(value);
            }
            if (profile.isPresent() && !profile.get().getId().equals(currentProfileId)) {
                conflicts.add(name);
            }
        });
        return conflicts;
    }

    private Set<ProfileDetail> mapToDetails(Map<String, String> details, Set<String> skippedDetails) {
        return details.entrySet().stream()
                .filter(entry -> !skippedDetails.contains(entry.getKey()))
                .map(entry -> new ProfileDetail(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private void logSkippedDetails(Long telegramUserId, Set<String> skippedDetails) {
        skippedDetails.forEach(detailName ->
                log.info("Skipping profile detail '{}' for telegramUserId {}: value is already used by another profile",
                        detailName, telegramUserId)
        );
    }

    private boolean isUniqueConstraintViolation(Throwable exception, String constraintName) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof SQLException sqlException
                    && UNIQUE_VIOLATION_SQL_STATE.equals(sqlException.getSQLState())
                    && sqlException.getMessage() != null
                    && sqlException.getMessage().contains(constraintName)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private Profile getProfileByTelegramUserId(Long telegramUserId) {
        return profileRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
    }
}