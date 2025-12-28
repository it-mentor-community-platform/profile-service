package com.itmentorcommunityplatform.profileservice.config;


import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "profile-service")
public class AchievementConfig {

    private  Map<AchievementType, String> achievements;

}
