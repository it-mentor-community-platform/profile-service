package com.itmentorcommunityplatform.profileservice.mapper;


import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.dto.AchievementDto;
import com.itmentorcommunityplatform.profileservice.dto.ProjectDto;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    Project projectCreatedEvent(ProjectCreatedEvent projectCreatedEvent);

    @Mapping(target = "id", ignore = true)
    Project projectDto(ProjectDto projectDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "authorTelegramUserId", target = "telegramUserId")
    AchievementDto achievementDto(ProjectCreatedEvent projectCreatedEvent);
}
