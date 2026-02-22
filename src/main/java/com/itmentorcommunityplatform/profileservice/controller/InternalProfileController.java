package com.itmentorcommunityplatform.profileservice.controller;

import com.itmentorcommunityplatform.profileservice.docs.GetProfileByGithubUrlDocs;
import com.itmentorcommunityplatform.profileservice.docs.GetProfileByTgUrlDocs;
import com.itmentorcommunityplatform.profileservice.docs.UpsertInternalProfileDocs;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpsertInternalRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileWithTelegramIdResponseDto;
import com.itmentorcommunityplatform.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile/internal")
@RequiredArgsConstructor
public class InternalProfileController {

    private final ProfileService profileService;

    @PostMapping("/profile")
    @UpsertInternalProfileDocs
    public ResponseEntity<Void> upsertProfile(
            @RequestBody ProfileUpsertInternalRequestDto dto) {
        boolean isCreated = profileService.upsertProfileInternal(dto);
        return isCreated
                ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.ok().build();
    }

    @GetMapping("/profile/by-github-profile-url")
    @GetProfileByGithubUrlDocs
    public ResponseEntity<ProfileWithTelegramIdResponseDto> getProfileByGitHubUrl(
            @RequestParam("url") String gitHubUrl) {

        ProfileWithTelegramIdResponseDto profile = profileService.getProfileByGitHubUrl(gitHubUrl);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profile/by-telegram-url")
    @GetProfileByTgUrlDocs
    public ResponseEntity<ProfileWithTelegramIdResponseDto> getProfileByTgUrl(
            @RequestParam("url") String tgUrl) {

        ProfileWithTelegramIdResponseDto profile = profileService.getProfileByTgUrl(tgUrl);

        return ResponseEntity.ok(profile);
    }
}
