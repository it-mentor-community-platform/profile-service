package com.itmentorcommunityplatform.profileservice.dto.request;

import java.util.List;

public record UserRolesRequest(Long telegramUserId, List<String> roleName) {
}
