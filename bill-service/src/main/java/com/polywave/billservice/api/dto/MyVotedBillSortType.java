package com.polywave.billservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "참여한 투표 안건 정렬 방식")
public enum MyVotedBillSortType {

    @Schema(description = "최근 투표순")
    LATEST,

    @Schema(description = "인기순")
    POPULAR
}