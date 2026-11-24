package com.itmentorcommunityplatform.profileservice.service;


import com.itmentorcommunityplatform.profileservice.domain.Profile;
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
        String normalizedGithubUrl = githubUrl.replaceFirst("/+$", "").replaceFirst("/[^/]+$", "");
        Map<String, String> details = Map.of(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName(), normalizedGithubUrl);
        profilePersistenceService.updateProfile(profile, details);
        log.info("GitHub URL processed: {}", githubUrl);
    }
}
