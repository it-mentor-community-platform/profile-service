package com.itmentorcommunityplatform.profileservice.controller;

import com.itmentorcommunityplatform.profileservice.docs.UpsertInternalProfileDocs;
import com.itmentorcommunityplatform.profileservice.dto.request.ProfileUpsertInternalRequestDto;
import com.itmentorcommunityplatform.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile/internal")
@RequiredArgsConstructor
public class InternalProfileController {

    private final ProfileService profileService;

    @PatchMapping("/profile")
    @UpsertInternalProfileDocs
    public ResponseEntity<Void> upsertProfile(
            @RequestBody ProfileUpsertInternalRequestDto dto){
        boolean isCreated = profileService.upsertProfile(dto);
        return isCreated
                ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.ok().build();
    }
}
