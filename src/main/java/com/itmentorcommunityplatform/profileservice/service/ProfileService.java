package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.dto.ProfileDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpsertInternalRequestDto;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMetrics profileMetrics;
    private final BaseProfileDetailValidator baseDetailValidator;
    private final ProfileDetailValidatorRegistry detailValidatorRegistry;

    private static final Pattern TELEGRAM_PATTERN = Pattern.compile("^https://t\\.me/[^\\s/]+$");
    private static final Pattern GITHUB_PATTERN = Pattern.compile("^https://github\\.com/[^\\s]+$");

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
                String githubUrl = dto.githubProfileUrl();
                String telegramUrl = dto.telegramUrl();
                if(githubUrl==null && telegramUrl==null){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No parameters found to update");
                }

                Profile profile = profileRepository.findByTelegramUserId(telegramUserId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

                if (validGithubUrl(githubUrl)) {
                    upsertDetail(profile,"github_profile",githubUrl);
                }else if (githubUrl != null){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Github profile url incorrect");
                }
                if (validTelegramUrl(telegramUrl)) {
                    upsertDetail(profile, "telegram_username", telegramUrl);
                }else if (telegramUrl != null){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Telegram profile url incorrect");
                }
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
        String githubUrl = null;
        String telegramUrl = null;
        if (details != null) {
            for (ProfileDetail detail : details) {
                if ("github_profile".equals(detail.getDetailName())) {
                    githubUrl = detail.getDetailValue();
                }
                if ("telegram_username".equals(detail.getDetailName())) {
                    String username = detail.getDetailValue();
                    if (username != null && !username.startsWith("https://t.me/")) {
                        telegramUrl = "https://t.me/" + username.replace("@", "");
                    }else {
                        telegramUrl = username;
                    }
                }
            }
        }
        return ProfileDto.builder()
                .githubProfileUrl(githubUrl)
                .telegramUrl(telegramUrl)
                .build();
    }

    private void upsertDetail(Profile profile, String detailName, String detailValue) {
        Optional<ProfileDetail> existing = profile.getDetails()
                .stream()
                .filter(d -> d.getDetailName().equals(detailName))
                .findFirst();
        if (existing.isPresent()) {
            existing.get().setDetailValue(detailValue);
        } else {
            profile.getDetails().add(
                    ProfileDetail.builder()
                            .detailName(detailName)
                            .detailValue(detailValue)
                            .build()
            );
        }
    }

    private boolean validTelegramUrl(String url) {
        return url != null && TELEGRAM_PATTERN.matcher(url).matches();
    }

    private boolean validGithubUrl(String url) {
        return url != null && GITHUB_PATTERN.matcher(url).matches();
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

            detailValidatorRegistry.getSpecificValidator(type.getDetailName())
                    .ifPresent(v -> v.validate(detailValue));
        }
    }
}
