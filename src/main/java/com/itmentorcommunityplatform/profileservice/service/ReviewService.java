package com.itmentorcommunityplatform.profileservice.service;

import com.itmentorcommunityplatform.profileservice.domain.Project;
import com.itmentorcommunityplatform.profileservice.domain.Review;
import com.itmentorcommunityplatform.profileservice.dto.ProjectDto;
import com.itmentorcommunityplatform.profileservice.dto.event.ReviewCreatedEvent;
import com.itmentorcommunityplatform.profileservice.repository.ProjectRepository;
import com.itmentorcommunityplatform.profileservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public void save(ReviewCreatedEvent event) {

        ProjectDto eventProject = event.getProject();

        Optional<Project> optionalProject = projectRepository.findByGithubRepositoryUrl(eventProject.githubRepositoryUrl());
        Project project = optionalProject.orElseGet(() -> projectRepository.save(Project.builder()
                .authorTelegramUserId(eventProject.authorTelegramUserId())
                .githubRepositoryUrl(eventProject.githubRepositoryUrl())
                .programmingLanguage(eventProject.programmingLanguage())
                .roadmapProject(eventProject.roadmapProject())
                .addedTimestamp(eventProject.addedTimestamp())
                .build()));

        reviewRepository.save(Review.builder()
                .id(event.getId())
                .project(project)
                .reviewerTelegramUserId(Long.valueOf(event.getReviewerTelegramUserId()))
                .url(event.getUrl())
                .timestamp(event.getAddedTimestamp())
                .build());
    }
}
