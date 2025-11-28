package com.itmentorcommunityplatform.profileservice.controller;

import com.itmentorcommunityplatform.profileservice.dto.ProfileDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileUpdateResponseDto;
import com.itmentorcommunityplatform.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileDto> getCurrentProfile(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId
    ) {
        ProfileDto profileDto = profileService.getCurrentUserProfile(telegramUserId);
        return ResponseEntity.ok(profileDto);
    }

    @PatchMapping
    public  ResponseEntity<ProfileDto> updateCurrentProfile(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId,
            @RequestBody ProfileUpdateRequestDto dto){
        var response = profileService.updateCurrentProfile(telegramUserId, dto);
        return ResponseEntity.ok(response);
    }
}