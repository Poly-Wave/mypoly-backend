package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "주소 검색 결과 응답")
public record AddressSearchResponse(
        @Schema(description = "전체 검색 결과 수", example = "1")
        int totalCount,

        @Schema(description = "현재 페이지 번호", example = "1")
        int currentPage,

        @Schema(description = "페이지당 출력 개수", example = "10")
        int countPerPage,

        @Schema(description = "주소 목록")
        List<AddressInfoResponse> addresses) {
}
