package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.domain.Review;
import com.itmentorcommunityplatform.profileservice.dto.ProjectDto;
import com.itmentorcommunityplatform.profileservice.dto.event.ReviewCreatedEvent;
import com.itmentorcommunityplatform.profileservice.exception.ValidationException;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import com.itmentorcommunityplatform.profileservice.repository.ReviewRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public void save(@NotNull @Valid ReviewCreatedEvent event) {

        ProjectDto eventProject = event.getProject();

        Optional<Project> optionalProject = projectRepository.findByGithubRepositoryUrl(eventProject.githubRepositoryUrl());
        Project project = optionalProject.orElseGet(
                () ->
                {
                    Project savedProject = projectRepository.save(Project.builder()
                            .authorTelegramUserId(eventProject.authorTelegramUserId())
                            .githubRepositoryUrl(eventProject.githubRepositoryUrl())
                            .programmingLanguage(eventProject.programmingLanguage())
                            .roadmapProject(eventProject.roadmapProject())
                            .addedTimestamp(eventProject.addedTimestamp())
                            .build());
                    log.info("Project with url {} was save to db", eventProject.githubRepositoryUrl());
                    return savedProject;
                }
        );

        reviewRepository.save(Review.builder()
                .projectId(project.getId())
                .reviewerTelegramUserId(parseTelegramId(event))
                .url(event.getUrl())
                .timestamp(event.getAddedTimestamp())
                .build());
    }

    private long parseTelegramId(ReviewCreatedEvent event) {
        try {
            return Long.parseLong(event.getReviewerTelegramUserId());
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid reviewer telegram id!");
        }
    }
}
