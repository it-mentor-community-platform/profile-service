package com.itmentorcommunityplatform.profileservice.controller;

import com.itmentorcommunityplatform.profileservice.docs.GetProfileByGithubUrl;
import com.itmentorcommunityplatform.profileservice.docs.UpsertInternalProfileDocs;
import com.itmentorcommunityplatform.profileservice.dto.ProfileResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpsertInternalRequestDto;
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
        boolean isCreated = profileService.upsertProfile(dto);
        return isCreated
                ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.ok().build();
    }

    @GetMapping("/profile/by-github-profile-url")
    @GetProfileByGithubUrl
    public ResponseEntity<ProfileResponseDto> getProfileByGitHubUrl(
            @RequestParam("url") String gitHubUrl) {

        ProfileResponseDto profile = profileService.getProfileByGitHubUrl(gitHubUrl);
        return ResponseEntity.ok(profile);
    }
}
