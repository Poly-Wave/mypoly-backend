package com.polywave.billservice.api.spec;

import com.polywave.billservice.api.dto.BillBookmarkSortType;
import com.polywave.billservice.api.dto.BookmarkedBillResponse;
import com.polywave.billservice.api.dto.SliceResponse;
import com.polywave.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Bill Bookmark", description = "의안 보관함 API")
@RequestMapping("/bookmarks")
public interface BillBookmarkApi {

    @Operation(summary = "보관함 안건 목록 조회", description = """
            로그인 사용자가 보관한 의안 목록을 조회합니다.

            - 날짜 필터는 '보관한 날짜' 기준입니다.
            - categoryCodes는 의안의 AI 카테고리 중 하나라도 매칭되면 포함됩니다.
            - stageCodes는 앱용 진행 단계 코드 기준입니다. 사용 가능 값: RECEIVED, REVIEW, DECISION, COMPLETED
            - sortType 기본값은 LATEST입니다.
            - 정렬은 pageable.sort가 아닌 sortType으로 제어합니다.
            """)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    ResponseEntity<SliceResponse<BookmarkedBillResponse>> getBookmarkedBills(
            @Parameter(description = "보관 시작일, KST 기준", example = "2026-01-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @Parameter(description = "보관 종료일, KST 기준", example = "2026-04-18")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @Parameter(description = "카테고리 코드 목록", example = "DIGITAL,MEDICAL")
            @RequestParam(required = false)
            Set<String> categoryCodes,

            @Parameter(description = "앱용 진행 단계 코드 목록", example = "REVIEW,DECISION")
            @RequestParam(required = false)
            Set<String> stageCodes,

            @Parameter(
                    description = "정렬 방식 (LATEST: 최근 보관순, OLDEST: 오래된 보관순)",
                    example = "LATEST",
                    schema = @Schema(
                            allowableValues = {"LATEST", "OLDEST"},
                            defaultValue = "LATEST"
                    )
            )
            @RequestParam(defaultValue = "LATEST")
            BillBookmarkSortType sortType,

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