package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.domain.type.RoadmapProjectType;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.SPRINTER)
public class SprinterAchievementStrategy implements AchievementCriteriaChecker {

    private final ProjectRepository projectRepository;

    @Override
    public boolean checkCriteria(ProjectCreatedEvent projectCreatedEvent) {

        Optional<Project> hangmanProject = projectRepository.findFirstByAuthorTelegramUserIdAndRoadmapProject(
                projectCreatedEvent.getAuthorTelegramUserId(),
                RoadmapProjectType.HANGMAN
        );

        Optional<Project> cloudProject = projectRepository.findFirstByAuthorTelegramUserIdAndRoadmapProject(
                projectCreatedEvent.getAuthorTelegramUserId(),
                RoadmapProjectType.CLOUD_FILE_STORAGE
        );

        if (hangmanProject.isEmpty() || cloudProject.isEmpty()){
            return false;
        }

        long hangmanTimestamp = hangmanProject.get().getAddedTimestamp();
        long cloudTimestamp = cloudProject.get().getAddedTimestamp();

        long diffMillis = Math.abs(cloudTimestamp - hangmanTimestamp);

        long sixMonthsInMillis = 6L * 30 * 24 * 60 * 60 * 1000;

        return diffMillis < sixMonthsInMillis;
    }
}
