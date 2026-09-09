package com.itmentorcommunityplatform.profileservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@AllArgsConstructor
@Builder
@Getter
@Table(name = "reviews")
public class Review {

    @Id
    @Setter
    private Long id;


    @Column("project_id")
    private Long projectId;

    @Column("reviewer_telegram_user_id")
    private Long reviewerTelegramUserId;

    @Column("url")
    private String url;

    @Column("added_timestamp")
    private Long timestamp;

}
