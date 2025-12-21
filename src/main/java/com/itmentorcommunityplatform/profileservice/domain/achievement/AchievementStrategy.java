package com.itmentorcommunityplatform.profileservice.domain.achievement;

import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AchievementStrategy {
    AchievementType type();
}
