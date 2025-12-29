package com.itmentorcommunityplatform.profileservice.domain.achievement;

import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;

public interface AchievementCriteriaChecker {

    boolean checkCriteria(ProjectCreatedEvent projectCreatedEvent);

}
