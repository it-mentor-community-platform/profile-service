package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.FIRST_PROJECT)
public class FirstProjectAchievementStrategy implements AchievementCriteriaChecker {

    private final ProjectRepository projectRepository;

    @Override
    public boolean checkCriteria(ProjectCreatedEvent projectCreatedEvent) {

        List<Project> projectsByUser = projectRepository.findByAuthorTelegramUserId(projectCreatedEvent.getAuthorTelegramUserId());

        return projectsByUser.size() == 1;
    }
}
