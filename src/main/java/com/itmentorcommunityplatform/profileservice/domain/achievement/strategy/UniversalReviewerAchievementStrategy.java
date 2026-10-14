package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.UNIVERSAL_REVIEWER)
public class UniversalReviewerAchievementStrategy implements AchievementCriteriaChecker {
    private final ProjectRepository projectRepository;

    @Override
    public boolean checkCriteria(Long userId) {
        Set<RoadmapProjectType> usersRoadmapProjects = projectRepository.findProjectsNamesByUserId(userId);

        return usersRoadmapProjects.size() >= 4;
    }
}
