package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.ALL_PROJECTS)
public class AllProjectsAchievementStrategy implements AchievementCriteriaChecker {

    private final ProjectRepository projectRepository;

    @Override
    public boolean checkCriteria(ProjectCreatedEvent projectCreatedEvent) {

        Set<String> requiredRoadmapProjects = Arrays.stream(RoadmapProjectType.values())
                .filter(project -> project != RoadmapProjectType.OTHER)
                .map(Enum::name)
                .collect(Collectors.toSet());

        Set<String> usersRoadmapProjects = projectRepository.findProjectsNamesByUserId(projectCreatedEvent.getAuthorTelegramUserId());

        return requiredRoadmapProjects.equals(usersRoadmapProjects);
    }
}
