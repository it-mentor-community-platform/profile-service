package com.itmentorcommunityplatform.profileservice.client;

import com.itmentorcommunityplatform.profileservice.dto.request.UserRolesRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "auth-service", url = "${feign.client.auth-service.url}")
public interface AuthServiceClient {

    @GetMapping("/internal/users")
    List<UserRolesRequest> getAllUsers(
            @RequestParam(value = "telegram_user_ids") List<Long> ids
    );
}
