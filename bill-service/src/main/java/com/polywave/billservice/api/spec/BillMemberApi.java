package com.polywave.billservice.api.spec;

import com.polywave.billservice.api.dto.BillMemberDetailResponse;
import com.polywave.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Bill Member", description = "국회의원 조회 API (로그인 필요)")
@RequestMapping("/members")
public interface BillMemberApi {

    @Operation(
            summary = "국회의원 상세 조회",
            description = """
                    국회의원 상세 화면에 필요한 기본 프로필 정보, 의원실 연락처, 보좌진 정보, 최근 대표 발의 의안 목록,
                    그리고 로그인 사용자와의 관심 분야 일치도(interestAffinity)를 반환합니다.

                    관심 분야 일치도
                    - 의원 비율: 의원이 발의(대표 + 공동)한 의안의 대표 카테고리 분포
                    - 사용자 비율: 사용자가 투표한 의안의 대표 카테고리 분포
                    - 일치도 = 카테고리마다 두 비율 중 작은 값을 취해 모두 합한 값 (0.0~1.0)
                    - AI 분석이 없어 카테고리가 없는 의안은 비율 계산에서 제외합니다.
                    - 의원의 발의 이력이나 사용자의 투표 이력이 없으면 일치도는 0.0 입니다.
                    - topCategories 는 의원의 발의 비율이 높은 상위 4개 카테고리입니다.
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BillMemberDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "국회의원을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{memberId}")
    ResponseEntity<BillMemberDetailResponse> getMemberDetail(
            @Parameter(description = "국회의원 ID", required = true, example = "1")
            @PathVariable Long memberId,
            @Parameter(hidden = true) Long userId
    );
}