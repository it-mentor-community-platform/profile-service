package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.dto.ProfileDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpsertInternalRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.event.UserCreatedEvent;
import com.itmentorcommunityplatform.profileservice.metrics.ProfileMetrics;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import com.itmentorcommunityplatform.profileservice.validator.base.BaseProfileDetailValidator;
import com.itmentorcommunityplatform.profileservice.validator.registry.ProfileDetailValidatorRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMetrics profileMetrics;
    private final BaseProfileDetailValidator baseDetailValidator;
    private final ProfileDetailValidatorRegistry detailValidatorRegistry;

    @Transactional
    public void createProfile(UserCreatedEvent event) {
        if (event == null || event.getTelegramUserId() == null) {
            log.warn("Received empty event or null telegramUserId. Skipping.");
            return;
        }
        Long telegramUserId = event.getTelegramUserId();
        log.info("Attempting to create profile for telegramUserId: {}", telegramUserId);

        if (profileRepository.findByTelegramUserId(telegramUserId).isPresent()) {
            log.info("Profile for telegramUserId: {} already exists. Skipping creation.", telegramUserId);
            return;
        }
        Profile newProfile = Profile.builder()
                .id(null)
                .telegramUserId(telegramUserId)
                .details(new HashSet<>())
                .build();

        profileRepository.save(newProfile);
        log.info("Successfully created profile with telegramUserId: {}", telegramUserId);
    }

    public ProfileDto getCurrentUserProfile(Long telegramUserId) {
        return profileMetrics.getGetProfileTimer().record(() -> {
            log.info("Fetching profile for telegramUserId: {}", telegramUserId);
            try {
                Profile profile = profileRepository.findByTelegramUserId(telegramUserId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
                profileMetrics.getGetProfileSuccessCounter().increment();
                return mapToDto(profile.getDetails());
            } catch (Exception e) {
                profileMetrics.getGetProfileErrorCounter().increment();
                throw e;
            }
        });
    }

    @Transactional
    public ProfileDto updateCurrentProfile(Long telegramUserId, ProfileUpdateRequestDto dto) {
        return profileMetrics.getGetProfileTimer().record(() -> {
            try {
                Map<String, String> newDetailsMap = dto.getDetails();
                validateDetails(newDetailsMap);

                Profile profile = profileRepository.findByTelegramUserId(telegramUserId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

                Set<ProfileDetail> mergedDetails = mergeProfileDetails(profile.getDetails(), newDetailsMap);
                profile.setDetails(mergedDetails);

                profileRepository.save(profile);
                profileMetrics.getGetProfileSuccessCounter().increment();
                return mapToDto(profile.getDetails());
            } catch (Exception e) {
                profileMetrics.getGetProfileErrorCounter().increment();
                throw e;
            }
        });
    }

    private ProfileDto mapToDto(Set<ProfileDetail> details) {
        Map<String, String> map = details.stream()
                .collect(Collectors.toMap(ProfileDetail::getDetailName, ProfileDetail::getDetailValue));

        return new ProfileDto(map);
    }

    @Transactional
    public boolean upsertProfile(ProfileUpsertInternalRequestDto dto) {

        Long telegramUserId = dto.telegramUserId();
        if (telegramUserId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'telegram_user_id' must be provided");
        }

        if (dto.details() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'details' must be provided");
        }
        Map<String, String> newDetailsMap = dto.details().getMap();
        validateDetails(newDetailsMap);

        Optional<Profile> foundProfile = profileRepository.findByTelegramUserId(telegramUserId);
        boolean isNewProfile = foundProfile.isEmpty();

        Set<ProfileDetail> existingDetails = foundProfile.map(Profile::getDetails).orElse(Collections.emptySet());

        Set<ProfileDetail> mergedDetails = mergeProfileDetails(existingDetails, newDetailsMap);

        Profile profile = Profile.builder()
                .id(foundProfile.map(Profile::getId).orElse(null))
                .telegramUserId(telegramUserId)
                .details(mergedDetails)
                .build();

        profileRepository.save(profile);

        return isNewProfile;
    }

    private static Set<ProfileDetail> mergeProfileDetails(
            Set<ProfileDetail> existingDetails,
            Map<String, String> newDetailsMap
    ) {
        Map<String, String> mergedMap = new HashMap<>();

        existingDetails.forEach(detail ->
                mergedMap.put(detail.getDetailName(), detail.getDetailValue()));

        mergedMap.putAll(newDetailsMap);

        return mergedMap.entrySet().stream()
                .map(e -> new ProfileDetail(e.getKey(), e.getValue()))
                .collect(Collectors.toSet());
    }

    private void validateDetails(Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile details should not be empty");
        }
        for (var entry : details.entrySet()) {
            String detailName = entry.getKey();
            String detailValue = entry.getValue();

            ProfileDetailType type = ProfileDetailType.fromName(detailName)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Unknown detail name: " + detailName
                    ));

            baseDetailValidator.validate(detailName, detailValue);

            detailValidatorRegistry.getSpecificValidator(type)
                    .ifPresent(v -> v.validate(detailValue));
        }
    }
}
