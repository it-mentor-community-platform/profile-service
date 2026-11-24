package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.SPRINTER)
public class SprinterAchievementStrategy implements AchievementCriteriaChecker {

    private static final long SIX_MONTHS_IN_MILLIS = 6L * 30 * 24 * 60 * 60 * 1000;
    private final ProjectRepository projectRepository;
    private final List<RoadmapProjectType> nonRequiredProjectsTypeForSprinter = List.of(RoadmapProjectType.OTHER,
            RoadmapProjectType.TASK_TRACKER);

    @Override
    public boolean checkCriteria(Long userId) {

        Set<RoadmapProjectType> requiredProjectsTypeForSprinter = EnumSet.allOf(RoadmapProjectType.class)
                .stream()
                .filter(type -> !nonRequiredProjectsTypeForSprinter.contains(type))
                .collect(Collectors.toSet());

        List<Project> allUserProjects = projectRepository.findByAuthorTelegramUserId(userId);

        List<Project> requiredUserProjects = new ArrayList<>();

        for (Project project : allUserProjects) {

            Optional<Project> projectWithSameType = requiredUserProjects.stream()
                    .filter(p -> p.getRoadmapProject() == project.getRoadmapProject())
                    .findFirst();

            if (projectWithSameType.isEmpty() && requiredProjectsTypeForSprinter.contains(project.getRoadmapProject())) {
                requiredUserProjects.add(project);
            }
        }


        Set<RoadmapProjectType> userProjectTypes = requiredUserProjects.stream()
                .map(Project::getRoadmapProject)
                .collect(Collectors.toSet());

        if (!userProjectTypes.containsAll(requiredProjectsTypeForSprinter)) {
            return false;
        }

        List<Project> sortedByTime = requiredUserProjects.stream()
                .sorted(Comparator.comparingLong(Project::getAddedTimestamp))
                .toList();


        Long firstProjectTimestamp = sortedByTime.getFirst().getAddedTimestamp();

        Long sixthProjectTimestamp = sortedByTime.get(5).getAddedTimestamp();

        long diffMillis = sixthProjectTimestamp - firstProjectTimestamp;

        return diffMillis < SIX_MONTHS_IN_MILLIS;
    }
}
