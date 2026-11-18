package com.itmentorcommunityplatform.profileservice.service;


import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.exception.ProfileNotFoundException;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileDetailService {

    private final ProfileRepository profileRepository;
    private final ProfilePersistenceService profilePersistenceService;

    public void upsertGithubProfileUrl(Long telegramUserId, String githubUrl) {
        Profile profile = profileRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));

        boolean githubAlreadyUpToDate = profile.getDetails().stream()
                .map(ProfileDetail::getDetailName)
                .anyMatch(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName()::equals);

        if (githubAlreadyUpToDate) {
            log.info("Github profile for profile with telegramId #{}, already exists", telegramUserId);
            return;
        }

        String normalizedGithubUrl = githubUrl.replaceFirst("/+$", "").replaceFirst("/[^/]+$", "");

        Map<String, String> details = Map.of(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName(), normalizedGithubUrl);
        profilePersistenceService.updateProfile(telegramUserId, details);
        log.info("GitHub URL processed: {}", githubUrl);
    }
}
