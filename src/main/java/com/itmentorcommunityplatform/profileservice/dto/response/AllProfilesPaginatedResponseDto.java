package com.itmentorcommunityplatform.profileservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AllProfilesPaginatedResponseDto {

    @JsonProperty("total_item_count")
    private final Long totalItemCount;

    @JsonProperty("total_page_count")
    private final Integer totalPageCount;

    @JsonProperty("page_size")
    private final Integer pageSize;

    @JsonProperty("page_number")
    private final Integer pageNumber;

    private final List<ProfileWithRolesResponseDto> items;
}
