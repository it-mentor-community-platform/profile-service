package com.itmentorcommunityplatform.profileservice.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project")
public class Project {

    @Id
    Long id;

    @Column("author_telegram_user_id")
    Long authorTelegramUserId;

    @Column("github_repository_url")
    String githubRepositoryUrl;

    @Column("programming_language")
    String programmingLanguage;

    @Column("roadmap_project")
    String roadmapProject;

    @Column("added_timestamp")
    Long addedTimestamp;

}
