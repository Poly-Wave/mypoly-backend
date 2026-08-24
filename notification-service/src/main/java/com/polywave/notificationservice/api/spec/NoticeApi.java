package com.polywave.notificationservice.api.spec;

import com.polywave.common.dto.ErrorResponse;
import com.polywave.common.example.CommonApiExamples;
import com.polywave.notificationservice.api.dto.NoticeDetailResponse;
import com.polywave.notificationservice.api.dto.NoticeListResponse;
import com.polywave.notificationservice.api.example.NoticeApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Notice", description = "공지사항 API")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/notices")
public interface NoticeApi {

    @Operation(summary = "공지사항 목록 조회", description = """
            노출 대상(is_visible=true) 공지사항을 최신순으로 반환합니다.

            - 공지사항이 없으면 빈 배열을 반환합니다. (404 가 아닙니다)
            - 등록일(displayDate)은 KST 기준 YYYY.MM.DD 입니다.
            - 페이지네이션은 hasNext 기반입니다.
            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NoticeListResponse.class),
                            examples = {
                                    @ExampleObject(name = "공지사항 있음", value = NoticeApiExamples.EXAMPLE_NOTICE_LIST_OK),
                                    @ExampleObject(name = "공지사항 없음", value = NoticeApiExamples.EXAMPLE_NOTICE_LIST_EMPTY)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED)
                    )
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NoticeListResponse> getNotices(
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "페이지 번호, 0부터 시작", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size
    );

    @Operation(summary = "공지사항 상세 조회", description = "공지사항 1건의 제목/본문/등록일을 반환합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NoticeDetailResponse.class),
                            examples = @ExampleObject(name = "조회 성공", value = NoticeApiExamples.EXAMPLE_NOTICE_DETAIL_OK)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "인증 필요", value = CommonApiExamples.EXAMPLE_UNAUTHORIZED)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "공지사항 없음(비공개 포함)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "공지사항 없음", value = NoticeApiExamples.EXAMPLE_NOTICE_NOT_FOUND)
                    )
            )
    })
    @GetMapping(value = "/{noticeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NoticeDetailResponse> getNotice(
            @Parameter(description = "공지사항 ID") @PathVariable Long noticeId,
            @Parameter(hidden = true) Long userId
    );
}
