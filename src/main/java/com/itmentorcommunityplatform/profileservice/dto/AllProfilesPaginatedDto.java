package com.itmentorcommunityplatform.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itmentorcommunityplatform.profileservice.domain.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AllProfilesPaginatedDto {

    @JsonProperty("total_item_count")
    private final Long totalItemCount;

    @JsonProperty("total_page_count")
    private final Integer totalPageCount;

    @JsonProperty("page_size")
    private final Integer pageSize;

    @JsonProperty("page_number")
    private final Integer pageNumber;

    private final List<ProfileResponseDto> items;
}
