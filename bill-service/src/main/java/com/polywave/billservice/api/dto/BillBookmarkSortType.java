package com.polywave.billservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보관함 안건 정렬 방식")
public enum BillBookmarkSortType {

    @Schema(description = "최근 보관순")
    LATEST,

    @Schema(description = "인기순")
    POPULAR
}