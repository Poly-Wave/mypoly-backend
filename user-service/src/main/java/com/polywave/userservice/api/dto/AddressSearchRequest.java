package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "주소 검색 요청", requiredProperties = {"keyword"})
public record AddressSearchRequest(
        @Schema(description = "검색어 (시도, 시군구, 읍면동)", example = "강남") @NotBlank(message = "검색어는 필수입니다.") @Size(min = 2, message = "검색어는 2자 이상이어야 합니다.") String keyword,

        @Schema(description = "현재 페이지 번호", example = "1", defaultValue = "1") @Positive(message = "페이지 번호는 1 이상이어야 합니다.") Integer currentPage,

        @Schema(description = "페이지당 출력 개수", example = "10", defaultValue = "10") @Positive(message = "페이지당 개수는 1 이상이어야 합니다.") Integer countPerPage) {

    public AddressSearchRequest {
        if (currentPage == null) {
            currentPage = 1;
        }
        if (countPerPage == null) {
            countPerPage = 10;
        }
    }
}
