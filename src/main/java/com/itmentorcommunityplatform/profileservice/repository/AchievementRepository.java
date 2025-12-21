package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import org.springframework.data.repository.CrudRepository;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {

    boolean existsByProfileIdAndAchievementType(Long profileId, AchievementType achievementType);
}