package com.itmentorcommunityplatform.profileservice.service;


import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileDetailService {

    private final ProfileRepository profileRepository;
    private final ProfileDetailMerger profileDetailMerger;

    public void upsertGithubProfileUrl(Long telegramUserId, String githubUrl) {
        Profile profile = profileRepository.findByTelegramUserId(telegramUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        Set<ProfileDetail> profileDetails = profile.getDetails();
        Map<String, String> newDetailsMap = new HashMap<>();

        AtomicBoolean githubAlreadyExists = new AtomicBoolean(true);

        profileDetails.forEach(profileDetail -> {
            if (profileDetail.getDetailName().equals(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName())){
                githubAlreadyExists.set(false);
            }
        });

        if (!githubAlreadyExists.get()){
            log.info("Github profile already exists");
            return;
        }

        newDetailsMap.put(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName(), githubUrl);
        profile.setDetails(profileDetailMerger.mergeProfileDetails(profile.getDetails(), newDetailsMap));
        profileRepository.save(profile);
        log.info("Github profile saved");

    }

}
