package com.itmentorcommunityplatform.profileservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profiles")
public class Profile {

    @Id
    private Long id;

    @Column("telegram_user_id")
    private Long telegramUserId;

    @MappedCollection(idColumn = "profile_id")
    @Builder.Default
    private Set<ProfileDetail> details = new HashSet<>();
}
