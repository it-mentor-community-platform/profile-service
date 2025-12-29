package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.ALL_PROJECTS)
public class AllProjectsAchievementStrategy implements AchievementCriteriaChecker {

    private final ProjectRepository projectRepository;

    @Override
    public boolean checkCriteria(Long userId) {

        Set<RoadmapProjectType> requiredRoadmapProjects = EnumSet.allOf(RoadmapProjectType.class)
                .stream()
                .filter(project -> project != RoadmapProjectType.OTHER)
                .collect(Collectors.toSet());

        Set<RoadmapProjectType> usersRoadmapProjects = projectRepository
                .findProjectsNamesByUserId(userId);

        return usersRoadmapProjects.containsAll(requiredRoadmapProjects);
    }
}
