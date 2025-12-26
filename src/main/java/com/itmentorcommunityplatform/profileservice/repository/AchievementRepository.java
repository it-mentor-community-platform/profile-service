package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {

    boolean existsByProfileIdAndAchievementType(Long profileId, AchievementType achievementType);

    List<Achievement> findAllByProfileIdAndPubliclyVisibleTrue(Long profileId);
}