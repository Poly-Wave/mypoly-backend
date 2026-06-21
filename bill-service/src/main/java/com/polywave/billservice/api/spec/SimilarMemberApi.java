package com.polywave.billservice.api.spec;

import com.polywave.billservice.api.dto.SimilarMemberResponse;
import com.polywave.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Similar Member", description = "나와 투표 성향이 유사한 국회의원 조회 API (로그인 필요)")
@RequestMapping("/members")
public interface SimilarMemberApi {

    @Operation(
            summary = "유사 성향 국회의원 조회",
            description = """
                    로그인 사용자의 의안 투표 결과와 국회의원 본회의 표결 기록을 같은 의안 기준으로 비교해,
                    입장(찬성/반대)이 일치한 비율이 높은 순으로 국회의원을 반환합니다.

                    - 매칭 규칙: 사용자 AGREE ↔ 의원 찬성, 사용자 DISAGREE ↔ 의원 반대. 기권/불참 표결은 비교에서 제외합니다.
                    - 함께 표결한 의안이 10건 이상인 국회의원만 대상으로 합니다.
                    - 비교 가능한 데이터(사용자 투표 ∩ 의원 본회의 표결)가 없으면 빈 배열을 반환합니다.
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/similar")
    ResponseEntity<List<SimilarMemberResponse>> getSimilarMembers(
            @Parameter(hidden = true) Long userId
    );
}
