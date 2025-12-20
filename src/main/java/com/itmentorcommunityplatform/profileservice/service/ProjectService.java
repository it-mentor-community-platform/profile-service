package com.itmentorcommunityplatform.profileservice.service;


import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.mapper.ProjectMapper;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public void createdProject(ProjectCreatedEvent projectCreated){



        Project project=projectMapper.projectCreatedEvent(projectCreated);
        projectRepository.save(project);
        log.info("The new project has been successfully created");
    }
}
