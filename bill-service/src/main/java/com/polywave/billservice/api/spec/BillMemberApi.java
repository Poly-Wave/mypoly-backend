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
                    국회의원 상세 화면에 필요한 기본 프로필 정보를 반환합니다.
                    대표 발의 의안 목록과 관심 분야 통계는 후속 API 확장에서 같은 응답에 추가할 예정입니다.
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "국회의원을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{memberId}")
    ResponseEntity<BillMemberDetailResponse> getMemberDetail(
            @Parameter(description = "국회의원 ID", required = true)
            @PathVariable Long memberId
    );
}