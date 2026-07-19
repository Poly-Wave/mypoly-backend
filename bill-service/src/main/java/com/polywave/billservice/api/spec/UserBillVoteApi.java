package com.polywave.billservice.api.spec;

import com.polywave.billservice.api.dto.MyVotedBillResponse;
import com.polywave.billservice.api.openapi.DateQueryParameter;
import com.polywave.billservice.api.dto.MyVotedBillSliceResponse;
import com.polywave.billservice.api.dto.MyVotedBillSortType;
import com.polywave.billservice.api.dto.SliceResponse;
import com.polywave.billservice.api.dto.UserBillVoteRequest;
import com.polywave.billservice.domain.UserVoteResult;
import com.polywave.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Vote", description = "사용자 의안 투표 API")
@RequestMapping("/votes")
public interface UserBillVoteApi {

    @Operation(summary = "의안 투표 저장/수정", description = """
            로그인 사용자의 의안 투표를 저장합니다.
            이미 투표한 의안이면 기존 투표 결과를 수정합니다.
            """)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "의안 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping(value = "/{billId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> voteOnBill(
            @Parameter(description = "의안 ID", required = true)
            @PathVariable Long billId,

            @Parameter(hidden = true)
            Long userId,

            @RequestBody @Valid UserBillVoteRequest request
    );

    @Operation(summary = "참여한 투표 안건 목록 조회", description = """
            로그인 사용자가 참여한 투표 안건 목록을 조회합니다.

            - proposalFromDate/proposalToDate는 안건 생성일 기준입니다.
            - votedFromDate/votedToDate는 사용자가 실제 투표한 날짜 기준입니다.
            - voteResults는 현재 사용자의 투표 결과 기준입니다. 사용 가능 값: AGREE, DISAGREE
            - sortType 기본값은 LATEST입니다.
            - 정렬은 pageable.sort가 아닌 sortType으로 제어합니다.
            - 사용 가능 값: LATEST, POPULAR
            - 각 항목은 AI 헤드라인(`headline`)을 포함합니다.
            """)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MyVotedBillSliceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SliceResponse<MyVotedBillResponse>> getMyVotedBills(
            @DateQueryParameter(
                    description = "안건 생성 시작일, " + DateQueryParameter.DATE_QUERY_DESCRIPTION_SUFFIX,
                    example = "2026-01-01")
            @RequestParam(required = false)
            LocalDate proposalFromDate,

            @DateQueryParameter(
                    description = "안건 생성 종료일, " + DateQueryParameter.DATE_QUERY_DESCRIPTION_SUFFIX,
                    example = "2026-04-18")
            @RequestParam(required = false)
            LocalDate proposalToDate,

            @DateQueryParameter(
                    description = "투표한 날짜 시작일, " + DateQueryParameter.DATE_QUERY_DESCRIPTION_SUFFIX,
                    example = "2026-01-01")
            @RequestParam(required = false)
            LocalDate votedFromDate,

            @DateQueryParameter(
                    description = "투표한 날짜 종료일, " + DateQueryParameter.DATE_QUERY_DESCRIPTION_SUFFIX,
                    example = "2026-04-18")
            @RequestParam(required = false)
            LocalDate votedToDate,

            @Parameter(description = "투표 결과 목록", example = "AGREE,DISAGREE")
            @RequestParam(required = false)
            Set<UserVoteResult> voteResults,

            @Parameter(
                    description = "정렬 방식 (LATEST: 최근 투표순, POPULAR: 인기순)",
                    example = "LATEST",
                    schema = @Schema(
                            allowableValues = {"LATEST", "POPULAR"},
                            defaultValue = "LATEST"
                    )
            )
            @RequestParam(defaultValue = "LATEST")
            MyVotedBillSortType sortType,

            @Parameter(hidden = true)
            Long userId,

            @Parameter(description = "페이지 번호, 0부터 시작", example = "0")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20")
            int size
    );
}