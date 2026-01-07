package com.itmentorcommunityplatform.profileservice.controller;

import com.itmentorcommunityplatform.profileservice.docs.GetCurrentProfileDocs;
import com.itmentorcommunityplatform.profileservice.docs.GetUserProfileByIdDocs;
import com.itmentorcommunityplatform.profileservice.docs.UpdateCurrentProfileDocs;
import com.itmentorcommunityplatform.profileservice.dto.ProfileDetailsResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.ProfileResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
import com.itmentorcommunityplatform.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @GetCurrentProfileDocs
    public ResponseEntity<ProfileDetailsResponseDto> getCurrentProfile(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId
    ) {
        ProfileDetailsResponseDto profileDetailsResponseDto = profileService.getCurrentUserProfile(telegramUserId);
        return ResponseEntity.ok(profileDetailsResponseDto);
    }

    @PatchMapping
    @UpdateCurrentProfileDocs
    public ResponseEntity<ProfileDetailsResponseDto> updateCurrentProfile(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId,
            @RequestHeader("X-Telegram-Username") Optional<String> telegramUsername,
            @RequestBody ProfileUpdateRequestDto dto) {


        if (dto.getDetails()!=null && dto.getDetails().containsKey("telegram_url")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "invalid telegram_url field in the body");
        }

        var response = profileService.updateCurrentProfile(telegramUserId, dto, telegramUsername.orElse(null));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @GetUserProfileByIdDocs
    public ResponseEntity<ProfileDetailsResponseDto> getUserProfile(@PathVariable("id") Long profileId) {
        ProfileDetailsResponseDto userProfile = profileService.getUserProfile(profileId);

        return ResponseEntity.ok(userProfile);
    }
}