package com.polywave.userservice.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "개별 주소 정보 (시도/시군구/읍면동)", requiredProperties = {"sido", "sigungu", "emdName"})
public record AddressInfoResponse(
        @Schema(description = "시도", example = "서울특별시") String sido,

        @Schema(description = "시군구", example = "강남구") String sigungu,

        @Schema(description = "읍면동", example = "역삼동") String emdName) {
}
