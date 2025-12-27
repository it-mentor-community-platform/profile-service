package com.itmentorcommunityplatform.profileservice.controller;

import com.itmentorcommunityplatform.profileservice.dto.request.AchievementsVisibleRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/achievement")
@RequiredArgsConstructor
public class AchievementController {

    @PostMapping("/type")
    public ResponseEntity<Void> setAchievementVisibility(
            @RequestBody AchievementsVisibleRequestDto visible) {


        return ResponseEntity.ok(null);
    }
}
