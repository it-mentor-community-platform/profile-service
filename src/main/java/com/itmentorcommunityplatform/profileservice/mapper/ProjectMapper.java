package com.itmentorcommunityplatform.profileservice.mapper;


import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project projectCreatedEvent(ProjectCreatedEvent projectCreatedEvent);
}
