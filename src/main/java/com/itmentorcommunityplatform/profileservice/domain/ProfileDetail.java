package com.itmentorcommunityplatform.profileservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profiles_details")
public class ProfileDetail {

    @Column("detail_name")
    private String detailName;

    @Column("detail_value")
    private String detailValue;
}