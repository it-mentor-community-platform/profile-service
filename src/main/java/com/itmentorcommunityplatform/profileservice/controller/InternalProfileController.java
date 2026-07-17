package com.itmentorcommunityplatform.profileservice.controller;

import com.itmentorcommunityplatform.profileservice.docs.GetProfileByGithubUrlDocs;
import com.itmentorcommunityplatform.profileservice.docs.GetProfileByTgUrlDocs;
import com.itmentorcommunityplatform.profileservice.docs.InsertInternalProfileDocs;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileInsertInternalRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileInsertInternalResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileWithTelegramIdResponseDto;
import com.itmentorcommunityplatform.profileservice.service.InternalProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile/internal/profile")
@RequiredArgsConstructor
public class InternalProfileController {

    private final InternalProfileService internalProfileService;

    @PostMapping
    @InsertInternalProfileDocs
    public ResponseEntity<ProfileInsertInternalResponseDto> insertProfile(
            @Valid @RequestBody ProfileInsertInternalRequestDto dto) {

        ProfileInsertInternalResponseDto profile = internalProfileService.insertProfileInternal(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @GetMapping("/by-github-profile-url")
    @GetProfileByGithubUrlDocs
    public ResponseEntity<ProfileWithTelegramIdResponseDto> getProfileByGitHubUrl(
            @RequestParam("url") String gitHubUrl) {

        ProfileWithTelegramIdResponseDto profile = internalProfileService.getProfileByGitHubUrl(gitHubUrl);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/by-telegram-url")
    @GetProfileByTgUrlDocs
    public ResponseEntity<ProfileWithTelegramIdResponseDto> getProfileByTgUrl(
            @RequestParam("url") String tgUrl) {

        ProfileWithTelegramIdResponseDto profile = internalProfileService.getProfileByTgUrl(tgUrl);
        return ResponseEntity.ok(profile);
    }
}
