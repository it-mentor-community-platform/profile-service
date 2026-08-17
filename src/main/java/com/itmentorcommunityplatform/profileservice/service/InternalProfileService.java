package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
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
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileHelperService profileHelperService;
    private final GithubProfileUrlValidator githubProfileUrlValidator;
    private final ProfileMapper profileMapper;
    private final TelegramProfileUrlValidator telegramProfileUrlValidator;

    @Transactional
    public ProfileInsertInternalResponseDto insertProfileInternal(ProfileInsertInternalRequestDto dto) {

        Long telegramUserId = dto.telegramUserId();

        log.info("Starting insert new profile for telegramId: {}", telegramUserId);

        Set<ProfileDetail> details = normalizeProfileDetails(dto.details());

        Profile profile = Profile.builder()
                .telegramUserId(telegramUserId)
                .details(details)
                .build();

        try {
            profileRepository.save(profile);
        } catch (DbActionExecutionException e) {
            if (e.getCause() instanceof DuplicateKeyException) {
                log.warn("Profile with TelegramID: {} already exist", telegramUserId);
                throw new ProfileAlreadyExistException("Profile with telegramUserId " + telegramUserId + " already exists");
            }
            throw e;
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

        Set<ProfileDetail> details = normalizeProfileDetails(dto.details());

        Profile profile = profileOptional
                .orElseGet(() ->
                        Profile.builder()
                                .telegramUserId(telegramUserId)
                                .build()
                );

        if (isExist) {
            details.addAll(profile.getDetails());
        }

        profile.setDetails(details);

        profileRepository.save(profile);

        String logMessage = isExist ? "Profile was updated: {}" : "New profile inserted: {}";

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

    private @NonNull Set<ProfileDetail> normalizeProfileDetails(ProfileInsertInternalRequestDto.Details detailsFromDto) {
        Map<String, String> detailsFromDtoRequest = detailsFromDto.getMap();
        profileHelperService.validateDetails(detailsFromDtoRequest);

        return detailsFromDtoRequest.entrySet().stream()
                .map(e -> new ProfileDetail(e.getKey(), e.getValue()))
                .collect(Collectors.toSet());
    }
}