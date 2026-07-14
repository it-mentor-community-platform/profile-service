package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpsertInternalRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileUpsertInternalResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileWithTelegramIdResponseDto;
import com.itmentorcommunityplatform.profileservice.exception.MissingProfileDetailsException;
import com.itmentorcommunityplatform.profileservice.exception.MissingTelegramUserIdException;
import com.itmentorcommunityplatform.profileservice.exception.ProfileAlreadyExistException;
import com.itmentorcommunityplatform.profileservice.exception.ProfileNotFoundException;
import com.itmentorcommunityplatform.profileservice.mapper.ProfileMapper;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import com.itmentorcommunityplatform.profileservice.validator.impl.GithubProfileUrlValidator;
import com.itmentorcommunityplatform.profileservice.validator.impl.TelegramProfileUrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileHelperService  profileHelperService;
    private final GithubProfileUrlValidator githubProfileUrlValidator;
    private final ProfileMapper profileMapper;
    private final TelegramProfileUrlValidator telegramProfileUrlValidator;

    @Transactional
    public ProfileUpsertInternalResponseDto insertProfileInternal(ProfileUpsertInternalRequestDto dto) {
        Long telegramUserId = dto.telegramUserId();
        if (telegramUserId == null) {
            throw new MissingTelegramUserIdException("'telegram_user_id' must be provided");
        }

        log.info("Starting insert new profile for telegramId: {}", telegramUserId);

        if (dto.details() == null) {
            throw new MissingProfileDetailsException("'details' must be provided");
        }
        Map<String, String> detailsMap = dto.details().getMap();
        profileHelperService.validateDetails(detailsMap);

        Set<ProfileDetail> details = detailsMap.entrySet().stream()
                .map(e -> new ProfileDetail(e.getKey(), e.getValue()))
                .collect(Collectors.toSet());

        Profile profile = Profile.builder()
                .telegramUserId(telegramUserId)
                .details(details)
                .build();

        try {
            profile = profileRepository.save(profile);
        } catch (DbActionExecutionException e) {
            if (e.getCause() instanceof DuplicateKeyException) {
                log.warn("Profile with TelegramID: {} already exist", telegramUserId);
                throw new ProfileAlreadyExistException("Profile with telegramUserId " + telegramUserId + " already exists");
            }
            throw e;
        }

        log.info("New profile inserted: {}", profile);

        return new ProfileUpsertInternalResponseDto(
                profile.getId(),
                profile.getTelegramUserId(),
                profileMapper.mapToProfileDetailsDto(profile.getDetails())
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
}