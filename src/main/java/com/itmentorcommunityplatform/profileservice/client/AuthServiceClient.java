package com.itmentorcommunityplatform.profileservice.client;

import com.itmentorcommunityplatform.profileservice.dto.external.UserWithRolesResponseDto;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "auth-service", url = "${feign.client.auth-service.url}")
public interface AuthServiceClient {

    @Retryable(retryFor = FeignException.class)
    @GetMapping("/internal/users")
    List<UserWithRolesResponseDto> getAllUsers(
            @RequestParam(value = "telegram_user_ids") List<Long> ids
    );
}
