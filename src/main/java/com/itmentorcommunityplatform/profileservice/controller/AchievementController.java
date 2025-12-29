package com.itmentorcommunityplatform.profileservice.controller;


import com.itmentorcommunityplatform.profileservice.docs.GetProfileAchievements;
import com.itmentorcommunityplatform.profileservice.dto.AchievementDto;
import com.itmentorcommunityplatform.profileservice.dto.request.AchievementsVisibleRequestDto;
import com.itmentorcommunityplatform.profileservice.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping("/achievements")
    @GetProfileAchievements
    @RequestMapping("/achievements")
    public ResponseEntity<List<AchievementDto>> getProfileAchievements(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId) {

        var allProfileAchievements = achievementService.getProfileAchievements(telegramUserId);
        return ResponseEntity.ok(allProfileAchievements);
    }

    @PatchMapping("/achievement/type/{type}")
    public ResponseEntity<AchievementDto> setAchievementVisibility(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId,
            @RequestBody AchievementsVisibleRequestDto visible,
            @PathVariable String type
    ) {

        AchievementDto achievementDto = achievementService
                .setAchievementPublicity(telegramUserId, visible, type);

        return ResponseEntity.status(HttpStatus.OK).body(achievementDto);
    }
}
