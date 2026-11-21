package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.dto.external.ProfileUpdateDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileInsertInternalRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileInsertInternalResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileWithTelegramIdResponseDto;
import com.itmentorcommunityplatform.profileservice.exception.ProfileAlreadyExistException;
import com.itmentorcommunityplatform.profileservice.exception.ProfileNotFoundException;
import com.itmentorcommunityplatform.profileservice.mapper.ProfileMapper;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import com.itmentorcommunityplatform.profileservice.validator.impl.GithubProfileUrlValidator;
import com.itmentorcommunityplatform.profileservice.validator.impl.TelegramProfileUrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileHelperService profileHelperService;
    private final GithubProfileUrlValidator githubProfileUrlValidator;
    private final ProfileMapper profileMapper;
    private final TelegramProfileUrlValidator telegramProfileUrlValidator;
    private final ProfilePersistenceService profilePersistenceService;

    public ProfileInsertInternalResponseDto insertProfileInternal(ProfileInsertInternalRequestDto dto) {

        Long telegramUserId = dto.telegramUserId();

        log.info("Starting insert new profile for telegramId: {}", telegramUserId);

        Map<String, String> details = validateAndTransformDetailsToMapFromDto(dto.details());

        Profile profile;

        try {
            profile = profilePersistenceService.createProfile(telegramUserId, details);
        } catch (DbActionExecutionException | DataIntegrityViolationException e) {
            log.warn("Profile with TelegramID: {} already exists", telegramUserId);
            throw new ProfileAlreadyExistException("Profile with telegramUserId " + telegramUserId + " already exists");
        }

        log.info("New profile inserted: {}", profile);

        return new ProfileInsertInternalResponseDto(
                profile.getId(),
                profile.getTelegramUserId(),
                profileMapper.mapToProfileDetailsDto(profile.getDetails())
        );
    }

    @Transactional
    public ProfileUpdateDto upsertProfileInternal(ProfileInsertInternalRequestDto dto) {
        Long telegramUserId = dto.telegramUserId();

        log.info("Starting upsert profile for telegramId: {}", telegramUserId);

        Optional<Profile> profileOptional = profileRepository.findByTelegramUserId(telegramUserId);
        boolean isExist = profileOptional.isPresent();

        Map<String, String> detailsFromDtoRequest = validateAndTransformDetailsToMapFromDto(dto.details());

        Profile profile;

        if (isExist) {
            Map<String, String> newDetails = new HashMap<>(detailsFromDtoRequest);
            profileOptional.get().getDetails().forEach(detail -> newDetails.remove(detail.getDetailName()));
            if (newDetails.isEmpty()) {
                profile = profileOptional.get();
            } else {
                profile = profilePersistenceService.updateProfile(profileOptional.get(), newDetails);
            }
        } else {
            profile = profilePersistenceService.createProfile(telegramUserId, detailsFromDtoRequest);
        }

        String logMessage = isExist ? "Profile was processed: {}" : "New profile inserted: {}";

        log.info(logMessage, profile);

        return new ProfileUpdateDto(new ProfileInsertInternalResponseDto(
                profile.getId(),
                profile.getTelegramUserId(),
                profileMapper.mapToProfileDetailsDto(profile.getDetails())),
                isExist
        );
    }

    public ProfileWithTelegramIdResponseDto getProfileByGitHubUrl(String gitHubUrl) {

        githubProfileUrlValidator.validate(gitHubUrl);

        log.info("Searching profile by GitHub URL: {}", gitHubUrl);

        Profile profile = profileRepository.findProfileByGitHubUrl(gitHubUrl)
                .orElseThrow(() -> {
                    log.warn("Profile with URL: {} not found", gitHubUrl);
                    return new ProfileNotFoundException("Profile not found");
                });


        return profileMapper.mapToProfileWithTelegramIdDto(profile.getTelegramUserId(), profile.getDetails());
    }

    public ProfileWithTelegramIdResponseDto getProfileByTgUrl(String tgUrl) {

        telegramProfileUrlValidator.validate(tgUrl);

        log.info("Searching profile by Telegram URL: {}", tgUrl);

        Profile profile = profileRepository.findProfileByTgUrl(tgUrl)
                .orElseThrow(() -> {
                    log.warn("Profile with URL: {} not found", tgUrl);
                    return new ProfileNotFoundException("Profile not found");
                });

        return profileMapper.mapToProfileWithTelegramIdDto(profile.getTelegramUserId(), profile.getDetails());
    }

    private Map<String, String> validateAndTransformDetailsToMapFromDto(ProfileInsertInternalRequestDto.Details detailsFromDto) {
        Map<String, String> detailsFromDtoRequest = detailsFromDto.getMap();
        profileHelperService.validateDetails(detailsFromDtoRequest);
        return detailsFromDtoRequest;
    }
}