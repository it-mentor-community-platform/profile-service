package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.ProfileDetail;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ProfileDetailRepository extends CrudRepository<ProfileDetail, Long> {

    @Query("""
            SELECT * from profiles_details
            WHERE (detail_name = 'telegram_url' AND detail_value = :telegramUrl)
            OR (detail_name = 'github_profile_url' AND detail_value = :gitHubUrl)
            AND (:profileId IS NULL OR profile_id <> :profileId)""")
    List<ProfileDetail> findConflictingDetails(@Param("telegramUrl") String telegramUrl,
                                                         @Param("gitHubUrl") String gitHubUrl,
                                                         @Param("profileId") Long profileId);

    Set<ProfileDetail> findAllByProfileId(Long profileId);
}