package com.itmentorcommunityplatform.profileservice.controller;

import com.itmentorcommunityplatform.profileservice.dto.request.AchievementsVisibleRequestDto;
import com.itmentorcommunityplatform.profileservice.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/achievement")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @PatchMapping("/type/{type}")
    public ResponseEntity<Void> setAchievementVisibility(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId,
            @RequestBody AchievementsVisibleRequestDto visible,
            @PathVariable String type
    ) {

        achievementService.setAchievementPublicity(telegramUserId, visible, type);

        return ResponseEntity.ok(null);
    }
}
