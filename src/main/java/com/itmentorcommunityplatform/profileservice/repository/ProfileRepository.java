package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {

    Optional<Profile> findByTelegramUserId(Long telegramUserId);

    @Query("""
            SELECT p.* FROM profiles p 
            JOIN profiles_details pd ON p.id = pd.profile_id
            WHERE pd.detail_name = 'github_profile_url' AND pd.detail_value = :gitHubUrl""")
    Optional<Profile> findProfileByGitHubUrl(@Param("gitHubUrl") String gitHubUrl);


    @Query("""
                SELECT p.*
                FROM profiles p
                JOIN profiles_details d ON p.id = d.profile_id
                WHERE (LOWER(d.detail_name), LOWER(d.detail_value)) IN (:pairs)
                GROUP BY p.id
                HAVING COUNT(*) = :filterCount
                ORDER BY p.id
                LIMIT :limit OFFSET :offset
            """)
    List<Profile> findByDetails(
            @Param("pairs") List<String[]> pairs,
            @Param("filterCount") int filterCount,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query("""
                SELECT COUNT(*)
                FROM (
                    SELECT p.id
                    FROM profiles p
                    JOIN profiles_details d ON p.id = d.profile_id
                    WHERE (LOWER(d.detail_name), LOWER(d.detail_value)) IN (:pairs)
                    GROUP BY p.id
                    HAVING COUNT(*) = :filterCount
                )
            """)
    Long countFiltered(
            @Param("pairs") List<String[]> pairs,
            @Param("filterCount") int filterCount
    );

    @Query("""
                SELECT p.*
                FROM profiles p
                ORDER BY p.id
                LIMIT :limit OFFSET :offset
            """)
    List<Profile> findAll(
            int limit,
            int offset
    );

}
