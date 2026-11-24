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
@Table(name = "profiles_details")
public class ProfileDetail {

    @Id
    private Long id;

    @Column("profile_id")
    private Long profileId;

    @Column("detail_name")
    private String detailName;

    @Column("detail_value")
    private String detailValue;

    public ProfileDetail(Long profileId, String detailName, String detailValue) {
        this.profileId = profileId;
        this.detailName = detailName;
        this.detailValue = detailValue;
    }
}