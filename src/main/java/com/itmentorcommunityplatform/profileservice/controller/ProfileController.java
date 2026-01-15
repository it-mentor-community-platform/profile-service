package com.itmentorcommunityplatform.profileservice.controller;

import com.itmentorcommunityplatform.profileservice.docs.GetAllProfilesDocs;
import com.itmentorcommunityplatform.profileservice.docs.GetCurrentProfileDocs;
import com.itmentorcommunityplatform.profileservice.docs.GetUserProfileByIdDocs;
import com.itmentorcommunityplatform.profileservice.docs.UpdateCurrentProfileDocs;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpdateRequestDto;
import com.itmentorcommunityplatform.profileservice.dto.response.AllProfilesPaginatedResponseDto;
import com.itmentorcommunityplatform.profileservice.dto.response.ProfileResponseDto;
import com.itmentorcommunityplatform.profileservice.service.ProfileService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @GetCurrentProfileDocs
    public ResponseEntity<ProfileResponseDto> getCurrentProfile(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId
    ) {
        ProfileResponseDto profileResponseDto = profileService.getCurrentUserProfile(telegramUserId);
        return ResponseEntity.ok(profileResponseDto);
    }

    @PatchMapping
    @UpdateCurrentProfileDocs
    public ResponseEntity<ProfileResponseDto> updateCurrentProfile(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId,
            @RequestHeader("X-Telegram-Username") Optional<String> telegramUsername,
            @RequestBody ProfileUpdateRequestDto dto) {


        if (dto.getDetails() != null && dto.getDetails().containsKey("telegram_url")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "invalid telegram_url field in the body");
        }

        var response = profileService.updateCurrentProfile(telegramUserId, dto, telegramUsername.orElse(null));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @GetUserProfileByIdDocs
    public ResponseEntity<ProfileResponseDto> getUserProfile(@PathVariable("id") Long profileId) {
        ProfileResponseDto userProfile = profileService.getUserProfile(profileId);

        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/admin/profiles")
    @GetAllProfilesDocs
    public ResponseEntity<AllProfilesPaginatedResponseDto> getAllProfiles(@RequestParam("page_size") @Min(1) int pageSize,
                                                                          @RequestParam("page_number") @Min(1) int pageNumber,
                                                                          @RequestParam(required = false) Map<String, String> detailFilters,
                                                                          @RequestHeader(value = "X-User-Roles", required = false) List<String> roles) {


        if (roles == null || roles.stream().noneMatch(r -> r.equalsIgnoreCase("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: missing ADMIN role in X-User-Roles header");
        }

        detailFilters.remove("page_size");
        detailFilters.remove("page_number");

        AllProfilesPaginatedResponseDto allProfiles = profileService.getAllProfiles(pageSize, pageNumber, detailFilters);

        return ResponseEntity.ok(allProfiles);
    }
}