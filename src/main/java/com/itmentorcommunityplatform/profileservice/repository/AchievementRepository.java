package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {

    boolean existsByProfileIdAndAchievementType(Long profileId, AchievementType achievementType);

    List<Achievement> findAchievemetsByProfileId(Long id);

    List<Achievement> findAllByProfileIdAndPubliclyVisibleTrue(Long profileId);
}