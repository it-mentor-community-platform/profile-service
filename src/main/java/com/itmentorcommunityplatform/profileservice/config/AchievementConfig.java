package com.itmentorcommunityplatform.profileservice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AchievementConfig {

    private  Map<String, String> achievements;


    public Map<String, String>  getAchievements(){
        return achievements;
    }

    public void setAchievements(Map<String, String> achievements){
        this.achievements=achievements;
    }
}
