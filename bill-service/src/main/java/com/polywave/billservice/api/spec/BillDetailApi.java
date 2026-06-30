package com.polywave.billservice.api.spec;

import com.polywave.billservice.api.dto.BillBookmarkStatusResponse;
import com.polywave.billservice.api.dto.BillDetailResponse;
import com.polywave.billservice.api.dto.BillStatusHistoryResponse;
import com.polywave.billservice.api.dto.BillVoteDetailResponse;
import com.polywave.billservice.api.dto.BillVoteSummaryResponse;
import com.polywave.billservice.api.dto.SimilarTopicBillResponse;
import com.polywave.billservice.api.dto.SimilarTopicSortType;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Bill Detail", description = "의안 상세 조회 API (로그인 필요)")
@RequestMapping
public interface BillDetailApi {

    @Operation(
            summary = "의안 상세 조회",
            description = "의안 상세 화면에 필요한 기본 정보, AI 요약, 카테고리, 현재 단계, 투표 요약, 보관 여부 정보를 반환합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "의안을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{billId}")
    ResponseEntity<BillDetailResponse> getBillDetail(
            @Parameter(description = "의안 ID", required = true)
            @PathVariable Long billId,
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "의안 보관하기", description = "현재 로그인 사용자의 보관함에 의안을 추가합니다. 이미 보관된 의안이면 그대로 성공 처리합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "보관 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "의안을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @org.springframework.web.bind.annotation.PostMapping("/{billId}/bookmark")
    ResponseEntity<BillBookmarkStatusResponse> bookmarkBill(
            @Parameter(description = "의안 ID", required = true)
            @PathVariable Long billId,
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "의안 보관 해제", description = "현재 로그인 사용자의 보관함에서 의안을 제거합니다. 이미 보관되어 있지 않아도 성공 처리합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "보관 해제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "의안을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{billId}/bookmark")
    ResponseEntity<BillBookmarkStatusResponse> unbookmarkBill(
            @Parameter(description = "의안 ID", required = true)
            @PathVariable Long billId,
            @Parameter(hidden = true) Long userId
    );

    @Operation(
            summary = "의안 투표 요약 조회",
            description = "현재 사용자의 투표 여부/투표값과 전체 찬반 집계 정보를 반환합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "의안을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{billId}/vote-summary")
    ResponseEntity<BillVoteSummaryResponse> getBillVoteSummary(
            @Parameter(description = "의안 ID", required = true)
            @PathVariable Long billId,
            @Parameter(hidden = true) Long userId
    );

    @Operation(
            summary = "의안 투표 상세 조회",
            description = """
                    의안 투표 참여자의 전체 찬반 집계와 연령대·성별 분포를 반환합니다.
                    vote-summary와 달리 인구통계 breakdown을 함께 제공합니다.
                    breakdown의 ratio 분모는 해당 구분값(연령대/성별)이 저장된 투표 수 합계입니다.
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "의안을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{billId}/vote-detail")
    ResponseEntity<BillVoteDetailResponse> getBillVoteDetail(
            @Parameter(description = "의안 ID", required = true)
            @PathVariable Long billId,
            @Parameter(hidden = true) Long userId
    );

    @Operation(
            summary = "유사 주제 의안 목록 조회",
            description = """
                    현재 의안의 대표 카테고리(rank 1) 기준으로 유사 주제 의안을 조회합니다.
                    sortType
                    - RELEVANT: 같은 대표 카테고리 + 최신순
                    - HOT_DEBATE: 같은 대표 카테고리 + 찬반이 팽팽한 순
                    - TRENDING: 같은 대표 카테고리 + 최근 7일 스냅샷 인기순
                    - MONTHLY_POPULAR: 같은 대표 카테고리 + 최근 30일 투표 수 순
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "의안을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{billId}/similar-topics")
    ResponseEntity<List<SimilarTopicBillResponse>> getSimilarTopics(
            @Parameter(description = "기준 의안 ID", required = true)
            @PathVariable Long billId,
            @Parameter(hidden = true) Long userId,
            @Parameter(
                    description = "정렬 방식",
                    schema = @Schema(implementation = SimilarTopicSortType.class),
                    example = "RELEVANT"
            )
            @RequestParam(defaultValue = "RELEVANT") SimilarTopicSortType sortType,
            @Parameter(description = "조회 개수", example = "5")
            @RequestParam(defaultValue = "5") Integer size
    );

    @Operation(
            summary = "의안 상태 이력 조회",
            description = "의안의 심사 진행 단계 이력을 반환합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "의안을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{billId}/status-history")
    ResponseEntity<List<BillStatusHistoryResponse>> getBillStatusHistory(
            @Parameter(description = "의안 ID", required = true)
            @PathVariable Long billId,
            @Parameter(hidden = true) Long userId
    );
}