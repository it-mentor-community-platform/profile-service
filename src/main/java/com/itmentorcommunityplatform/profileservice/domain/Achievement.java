package com.itmentorcommunityplatform.profileservice.domain;

import com.itmentorcommunityplatform.profileservice.domain.type.AchievementType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "achievements")
public class Achievement {

    @Id
    private Long id;

    @Column("profile_id")
    private Long profileId;

    @Column("achievement_type")
    private AchievementType achievementType;

    @Column("earned_timestamp")
    private Long earnedTimestamp;

    @Column("publicly_visible")
    private boolean publiclyVisible;
}
