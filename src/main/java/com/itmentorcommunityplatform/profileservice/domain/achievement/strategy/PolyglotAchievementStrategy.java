package com.itmentorcommunityplatform.profileservice.domain.achievement.strategy;

import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementCriteriaChecker;
import com.itmentorcommunityplatform.profileservice.domain.achievement.AchievementStrategy;
import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@AchievementStrategy(type = AchievementType.POLYGLOT)
public class PolyglotAchievementStrategy implements AchievementCriteriaChecker {

    private final ProjectRepository projectRepository;

    @Override
    public boolean checkCriteria(ProjectCreatedEvent projectCreatedEvent) {
        Set<String> usersProjectsLanguages = projectRepository.findUsersProjectsLanguagesByUserId(projectCreatedEvent.getAuthorTelegramUserId());

        return usersProjectsLanguages.size()>=4;
    }
}
