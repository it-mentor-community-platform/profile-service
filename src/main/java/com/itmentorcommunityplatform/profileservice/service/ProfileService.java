package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import com.itmentorcommunityplatform.profileservice.dto.ProfileDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
import com.itmentorcommunityplatform.profileservice.metrics.ProfileMetrics;
import com.itmentorcommunityplatform.profileservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMetrics profileMetrics;

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
}
