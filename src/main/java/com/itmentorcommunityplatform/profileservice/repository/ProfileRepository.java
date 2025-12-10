package com.itmentorcommunityplatform.profileservice.repository;

import com.itmentorcommunityplatform.profileservice.domain.Profile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {

    Optional<Profile> findByTelegramUserId(Long telegramUserId);

    @Query("""
            SELECT p.* FROM profiles p 
            JOIN profiles_details pd ON p.id = pd.profile_id
            WHERE pd.detail_name = 'github_profile_url' AND pd.detail_value = :gitHubUrl""")
    Optional<Profile> findProfileByGitHubUrl(@Param("gitHubUrl") String gitHubUrl);
}
