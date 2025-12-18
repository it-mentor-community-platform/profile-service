package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Achievement;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {

    @Query("""
            SELECT COUNT(*) > 0
            FROM achievements
            WHERE profile_id = :profileId AND achievement_type = :achievementType
            """)
    boolean existsByProfileIdAndAchievementType(@Param("profileId") Long profileId,
                                                @Param("achievementType") AchievementType achievementType
    );
}