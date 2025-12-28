package com.itmentorcommunityplatform.profileservice.controller;


import com.itmentorcommunityplatform.profileservice.docs.GetProfileAchievements;
import com.itmentorcommunityplatform.profileservice.dto.AchievementDto;
import com.itmentorcommunityplatform.profileservice.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping
    @GetProfileAchievements
    public ResponseEntity<List<AchievementDto>> getProfileAchievements(
            @RequestHeader("X-Telegram-User-Id") Long telegramUserId){

        var allProfileAchievements=achievementService.getProfileAchievements(telegramUserId);
        return ResponseEntity.ok(allProfileAchievements);
    }
}
