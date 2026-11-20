package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.domain.type.ProfileDetailType;
import com.itmentorcommunityplatform.profileservice.repository.ProfileDetailRepository;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilePersistenceService {
    private final ProfileRepository profileRepository;
    private final ProfileDetailRepository profileDetailRepository;

    @Transactional
    public Profile createProfile(Long telegramUserId, Map<String, String> details) {
        Profile profile = profileRepository.save(new Profile(telegramUserId, null));
        List<ProfileDetail> conflicts = profileDetailRepository.findConflictingDetails(
                details.get(ProfileDetailType.TELEGRAM_URL.getDetailName()),
                details.get(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName()), profile.getId());
        Map<String, String> filteredDetails = filterAndLogConflictingDetails(profile.getId(), conflicts, details);
        profileDetailRepository.saveAll(mapToDetails(profile.getId(), filteredDetails));
        profile.setDetails(profileDetailRepository.findAllByProfileId(profile.getId()));
        return profile;
    }

    @Transactional
    public Profile updateProfile(Profile profile, Map<String, String> updatedDetails) {
        List<ProfileDetail> conflicts = profileDetailRepository.findConflictingDetails(
                updatedDetails.get(ProfileDetailType.TELEGRAM_URL.getDetailName()),
                updatedDetails.get(ProfileDetailType.GITHUB_PROFILE_URL.getDetailName()), profile.getId());
        Map<String, String> filteredDetails = filterAndLogConflictingDetails(profile.getId(), conflicts, updatedDetails);
        Set<ProfileDetail> existingDetails = profileDetailRepository.findAllByProfileId(profile.getId());
        List<ProfileDetail> mergedDetails = mergeDetails(profile.getId(), existingDetails, filteredDetails);
        profileDetailRepository.saveAll(mergedDetails);
        profile.setDetails(profileDetailRepository.findAllByProfileId(profile.getId()));
        return profile;
    }

    private List<ProfileDetail> mergeDetails(Long profileId, Set<ProfileDetail> existingDetails,
                                             Map<String, String> updatedDetails) {
        Map<String, ProfileDetail> mergedDetails = new LinkedHashMap<>();
        existingDetails.forEach(detail -> mergedDetails.put(detail.getDetailName(), detail));
        updatedDetails.forEach((name, value) -> {
            ProfileDetail existingDetail = mergedDetails.get(name);
            if (existingDetail != null) {
                existingDetail.setDetailValue(value);
            } else {
                mergedDetails.put(name, new ProfileDetail(profileId, name, value));
            }
        });
        return new ArrayList<>(mergedDetails.values());
    }

    private Set<ProfileDetail> mapToDetails(Long profileId, Map<String, String> details) {
        return details.entrySet().stream()
                .map(entry -> new ProfileDetail(profileId, entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private Map<String, String> filterAndLogConflictingDetails(Long profileId, List<ProfileDetail> conflicts,
                                                               Map<String, String> details) {
        Map<String, String> filteredDetails = new HashMap<>(details);
        for (ProfileDetail conflict : conflicts) {
            log.info("Skipping profile detail '{}' for profile ID {}: value is already used by another profile",
                    conflict.getDetailName(), profileId);
            filteredDetails.remove(conflict.getDetailName());
        }
        return filteredDetails;
    }
}