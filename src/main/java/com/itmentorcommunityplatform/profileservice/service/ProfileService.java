package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.dto.ProfileDto;
import com.itmentorcommunityplatform.profileservice.metrics.ProfileMetrics;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMetrics profileMetrics;

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
                    if (username != null) {
                        telegramUrl = "https://t.me/" + username.replace("@", "");
                    }
                }
            }
        }
        return ProfileDto.builder()
                .githubProfileUrl(githubUrl)
                .telegramUrl(telegramUrl)
                .build();
    }
}
