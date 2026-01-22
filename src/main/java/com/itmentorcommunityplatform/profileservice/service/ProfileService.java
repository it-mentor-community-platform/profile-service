package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.client.AuthServiceClient;
import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.domain.type.Role;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.dto.event.UserCreatedEvent;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpsertInternalRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.request.UserRolesRequest;
import com.itmentorcommunityplatform.profileservice.dto.response.*;
import com.itmentorcommunityplatform.profileservice.dto.response.AllProfilesPaginatedResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileWithAchievementsResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileWithTelegramIdResponseDto;
import com.itmentorcommunityplatform.profileservice.mapper.ProfileMapper;
import com.itmentorcommunityplatform.profileservice.metrics.ProfileMetrics;
import com.itmentorcommunityplatform.profileservice.repository.AchievementRepository;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import com.itmentorcommunityplatform.profileservice.validator.base.BaseProfileDetailValidator;
import com.itmentorcommunityplatform.profileservice.validator.impl.GithubProfileUrlValidator;
import com.itmentorcommunityplatform.profileservice.validator.registry.ProfileDetailValidatorRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    @Autowired
    @Lazy
    private final ProfileService self;
    private final ProfileRepository profileRepository;
    private final AchievementRepository achievementRepository;
    private final ProfileMetrics profileMetrics;
    private final BaseProfileDetailValidator baseDetailValidator;
    private final ProfileDetailValidatorRegistry detailValidatorRegistry;
    private final GithubProfileUrlValidator githubProfileUrlValidator;
    private final ProfileMapper profileMapper;
    private final AuthServiceClient authServiceClient;


    @Transactional
    public void createOrUpdateProfile(UserCreatedEvent event) {
        if (event == null || event.getTelegramUserId() == null) {
            log.warn("Received empty event or null telegramUserId. Skipping.");
            return;
        }

        Long telegramUserId = event.getTelegramUserId();
        log.info("Attempting to create profile for telegramUserId: {}", telegramUserId);

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
            Profile existingProfile = maybeProfile.get();

            Set<ProfileDetail> profileDetails = mergeProfileDetails(existingProfile.getDetails(), details);
            existingProfile.setDetails(profileDetails);

            profileRepository.save(existingProfile);
        } else {
            log.info("Creating new profile for telegramUserId: {}", telegramUserId);

            Set<ProfileDetail> newProfileDetails = details.entrySet().stream()
                    .map(e -> new ProfileDetail(e.getKey(), e.getValue()))
                    .collect(Collectors.toSet());

            Profile newProfile = Profile.builder()
                    .id(null)
                    .telegramUserId(telegramUserId)
                    .details(newProfileDetails)
                    .build();

            profileRepository.save(newProfile);
            log.info("Successfully created profile with telegramUserId: {}", telegramUserId);
        }
    }


    public ProfileWithAchievementsResponseDto getCurrentUserProfile(Long telegramUserId) {
        return profileMetrics.getGetProfileTimer().record(() -> {
            log.info("Fetching profile for telegramUserId: {}", telegramUserId);
            try {
                Profile profile = profileRepository.findByTelegramUserId(telegramUserId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

                List<Achievement> achievements = achievementRepository.findAllByProfileIdAndPubliclyVisibleTrue(profile.getId());

                profileMetrics.getGetProfileSuccessCounter().increment();

                return profileMapper.mapToProfileWithAchievementsDto(profile.getDetails(), achievements);

            } catch (Exception e) {
                profileMetrics.getGetProfileErrorCounter().increment();
                throw e;
            }
        });
    }

    @Transactional
    public ProfileWithAchievementsResponseDto updateCurrentProfile(Long telegramUserId, ProfileUpdateRequestDto dto, String telegramUsername) {
        return profileMetrics.getGetProfileTimer().record(() -> {
            try {
                Map<String, String> newDetailsMap = dto.getDetails();
                validateDetails(newDetailsMap);

                if (telegramUsername != null && !telegramUsername.isBlank()) {
                    newDetailsMap.put("telegram_url", "https://t.me/" + telegramUsername);
                }

                Profile profile = profileRepository.findByTelegramUserId(telegramUserId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

                List<Achievement> achievements = achievementRepository.findAllByProfileIdAndPubliclyVisibleTrue(profile.getId());

                Set<ProfileDetail> mergedDetails = mergeProfileDetails(profile.getDetails(), newDetailsMap);
                profile.setDetails(mergedDetails);

                profileRepository.save(profile);
                profileMetrics.getGetProfileSuccessCounter().increment();

                return profileMapper.mapToProfileWithAchievementsDto(profile.getDetails(), achievements);

            } catch (Exception e) {
                profileMetrics.getGetProfileErrorCounter().increment();
                throw e;
            }
        });
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

    public ProfileWithTelegramIdResponseDto getProfileByGitHubUrl(String gitHubUrl) {

        githubProfileUrlValidator.validate(gitHubUrl);

        log.info("Searching profile by GitHub URL: {}", gitHubUrl);

        Profile profile = profileRepository.findProfileByGitHubUrl(gitHubUrl)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile with URL: %s not found".formatted(gitHubUrl)
                ));

        return profileMapper.mapToProfileWithTelegramIdDto(profile.getTelegramUserId(), profile.getDetails());
    }

    @Transactional(readOnly = true)
    public Optional<Profile> getProfileForEvent(ProjectCreatedEvent event) {
        if (event == null || event.getAuthorTelegramUserId() == null) {
            log.warn("Received empty event or null author ID");
            return Optional.empty();
        }

        Long telegramUserId = event.getAuthorTelegramUserId();

        Optional<Profile> maybeProfile = profileRepository.findByTelegramUserId(telegramUserId);

        if (maybeProfile.isEmpty()) {
            log.warn("Profile not found for telegramUserId {}", telegramUserId);
            return Optional.empty();
        }

        return maybeProfile;
    }

    public ProfileWithAchievementsResponseDto getUserProfile(Long profileId) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile with id: %s not found".formatted(profileId)
                ));

        List<Achievement> achievements = achievementRepository.findAllByProfileIdAndPubliclyVisibleTrue(profileId);

        return profileMapper.mapToProfileWithAchievementsDto(profile.getDetails(), achievements);
    }

    @Transactional(readOnly = true)
    public Profile getProfileByTelegramIdOrThrow(Long telegramUserId) {
        return profileRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> {
                    log.warn("Profile not found for telegramUserId: {}", telegramUserId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Profile with Telegram-User-Id %s does not exist".formatted(telegramUserId));
                });
    }

    public AllProfilesPaginatedResponseDto getAllProfiles(Integer pageSize, Integer pageNumber, Map<String, String> detailFilters) {
    private Map<String, String> getIdentityInfoIfPresent(UserCreatedEvent event) {
        Map<String, String> details = new HashMap<>();

        if (event.getFirstName() != null && !event.getFirstName().isBlank()) {
            details.put(ProfileDetailType.FIRST_NAME.getDetailName(), event.getFirstName());
        }
        if (event.getLastName() != null && !event.getLastName().isBlank()) {
            details.put(ProfileDetailType.LAST_NAME.getDetailName(), event.getLastName());
        }
        return details;
    }

    public AllProfilesPaginatedResponseDto getAllProfiles(Integer pageSize,
                                                          Integer pageNumber,
                                                          Map<String, String> detailFilters) {

        List<Profile> allProfilesPaginated;
        Long allProfilesCount;
        int totalPageCount;

        int offset = (pageNumber - 1) * pageSize;

        if (detailFilters == null || detailFilters.isEmpty()) {
            allProfilesPaginated = profileRepository.findAll(pageSize, offset);

            allProfilesCount = profileRepository.count();

        } else {

            validateDetails(detailFilters);

            List<String[]> detailFiltersList = detailFilters.entrySet()
                    .stream().map(e -> new String[]{e.getKey().toLowerCase(), e.getValue().toLowerCase()})
                    .toList();

            allProfilesPaginated = profileRepository.findByDetails(detailFiltersList,
                    detailFiltersList.size(),
                    pageSize,
                    offset);

            allProfilesCount = profileRepository.countFiltered(detailFiltersList, detailFiltersList.size());

        }

        totalPageCount = Math.max((int) Math.ceil((double) allProfilesCount / pageSize), 1);

        if (pageNumber > totalPageCount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number is greater than total page count");
        }

        List<ProfileWithTelegramIdResponseDto> profilesWithTelegramId = allProfilesPaginated.stream()
                .map((profile -> profileMapper.mapToProfileWithTelegramIdDto(profile.getTelegramUserId(), profile.getDetails())))
                .toList();

        return new AllProfilesPaginatedResponseDto(allProfilesCount, totalPageCount, allProfilesPaginated.size(), pageNumber, profilesWithTelegramId);
        List<ProfileWithRolesResponseDto> items;

        try {
            items = self.enrichProfilesWithRoles(allProfilesPaginated);
        } catch (Exception ex) {
            log.error("All attempts to fetch user roles failed. Reason: {}", ex.getMessage());
            throw ex;
        }

        return new AllProfilesPaginatedResponseDto(allProfilesCount, totalPageCount, allProfilesPaginated.size(), pageNumber, items);
    }

    @CircuitBreaker(name = "auth-service")
    @Retry(name = "auth-service")
    List<ProfileWithRolesResponseDto> enrichProfilesWithRoles(List<Profile> profiles) {
        List<Long> telegramIds = profiles.stream()
                .map(Profile::getTelegramUserId)
                .toList();

        List<UserRolesRequest> userRoles = authServiceClient.getAllUsers(telegramIds);

        Map<Long, List<Role>> rolesById = userRoles.stream()
                .collect(Collectors.toMap(
                        UserRolesRequest::telegramUserId,
                        user -> user.roleName().stream()
                                .map(role -> Role.valueOf(role.toUpperCase()))
                                .toList()
                ));

        return profiles.stream()
                .map(profile -> new ProfileWithRolesResponseDto(
                        profile.getId(),
                        new ProfileDetailsResponseDto(profile.getDetails().stream()
                                .collect(Collectors.toMap(
                                        ProfileDetail::getDetailName,
                                        ProfileDetail::getDetailValue
                                ))),
                        rolesById.get(profile.getTelegramUserId())
                )).toList();
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

        details.forEach((detailName, detailValue) -> {
            ProfileDetailType type = ProfileDetailType.fromName(detailName)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Unknown detail name: " + detailName
                    ));

            baseDetailValidator.validate(detailName, detailValue);

            detailValidatorRegistry.getSpecificValidator(type)
                    .ifPresent(v -> v.validate(detailValue));
        });

    }

}