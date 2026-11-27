package com.itmentorcommunityplatform.profileservice.service;


import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectCreatedEvent;
import com.itmentorcommunityplatform.profileservice.dto.event.ProjectUpdatedEvent;
import com.itmentorcommunityplatform.profileservice.exception.ProjectNotFoundException;
import com.itmentorcommunityplatform.profileservice.mapper.ProjectMapper;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public void createdProject(ProjectCreatedEvent projectCreated) {
        Project project = projectMapper.projectCreatedEvent(projectCreated);
        projectRepository.save(project);
        log.info("The new project has been successfully created");
    }

    @Transactional
    public void updateProject(ProjectUpdatedEvent projectUpdated) {
        String previousUrl = projectUpdated.getPreviousGithubRepositoryUrl();
        String newUrl = projectUpdated.getGithubRepositoryUrl();

        Project project = projectRepository.findByGithubRepositoryUrl(previousUrl)
                .orElseThrow(() -> new ProjectNotFoundException(
                        "Project not found by previous GitHub repository URL: ".concat(previousUrl)
                ));

        if (Objects.equals(newUrl, previousUrl)) {
            log.debug("Project update requires no changes: projectId={}", project.getId());
            return;
        }

        project.setGithubRepositoryUrl(newUrl);
        projectRepository.save(project);

        log.info("Project repository URL updated: projectId={}, previousUrl={}, newUrl={}",
                project.getId(), previousUrl, newUrl);
    }
}
