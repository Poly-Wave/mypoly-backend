package com.polywave.billservice.api.spec;

import com.polywave.billservice.api.dto.AgendaResponse;
import com.polywave.billservice.api.dto.AgendaSliceResponse;
import com.polywave.billservice.api.dto.AgendaTabResponse;
import com.polywave.billservice.api.dto.InterestAgendaResponse;
import com.polywave.billservice.api.dto.InterestAgendaSliceResponse;
import com.polywave.billservice.api.dto.MainAgendaResponse;
import com.polywave.billservice.api.dto.MainAgendaSliceResponse;
import com.polywave.billservice.api.dto.MainAgendaSortType;
import com.polywave.billservice.api.dto.PopularAgendaResponse;
import com.polywave.billservice.api.dto.SearchAgendaResponse;
import com.polywave.billservice.api.dto.SearchAgendaSliceResponse;
import com.polywave.billservice.api.dto.SliceResponse;
import com.polywave.billservice.api.example.AgendaApiExamples;
import com.polywave.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Agenda", description = "안건 탭 및 탭별 안건 목록 API (로그인 필요)")
@RequestMapping("/agendas")
public interface AgendaApi {

    @Operation(summary = "탭 목록 조회", description = "안건 목록에 사용할 탭(쟁쟁한, 요즘 핫한, 최근 30일, 내 또래) 메타 정보를 반환합니다. 로그인한 사용자만 호출 가능합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AgendaTabResponse.class)),
                            examples = @ExampleObject(
                                    name = "조회 성공",
                                    value = AgendaApiExamples.EXAMPLE_GET_TABS_OK
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요 (JWT 누락/만료/위조)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/tabs")
    ResponseEntity<List<AgendaTabResponse>> getTabs();

    @Operation(summary = "탭별 안건 목록 조회", description = """
            탭 코드에 해당하는 안건 목록을 반환합니다. 로그인한 사용자만 호출 가능합니다.
            - HOT_DEBATE: 쟁쟁한 (찬반 비율이 팽팽한 순)
            - TRENDING: 요즘 핫한 (최근 7일 투표 완료 수 순, 배치 선계산)
            - RECENT_30D: 최근 30일 (최근 30일 이내 투표가 최소 M건 이상인 의안만, M은 쟁쟁한과 동일 bill.agenda.hot-debate.min-vote-count, 해당 기간 투표 수 많은 순)
            - SAME_AGE: 내 또래 (최근 7일 이내 투표 중 동일 연령대 투표 10건 이상인 의안만, 투표 수 많은 순. 일수·최소 투표 수는 쟁쟁한과 동일 설정)
            """)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgendaSliceResponse.class),
                            examples = @ExampleObject(
                                    name = "조회 성공",
                                    value = AgendaApiExamples.EXAMPLE_GET_AGENDAS_BY_TAB_OK
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "알 수 없는 탭 코드",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요 (JWT 누락/만료/위조)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/tabs/{tabCode}")
    ResponseEntity<SliceResponse<AgendaResponse>> getAgendasByTab(
            @Parameter(description = "탭 코드 (HOT_DEBATE, TRENDING, RECENT_30D, SAME_AGE)", required = true)
            @PathVariable String tabCode,
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "페이지 번호, 0부터 시작", example = "0")
            @RequestParam(defaultValue = "0")
            int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20")
            int size
    );

    @Operation(summary = "안건 메인 목록 조회", description = """
            안건 메인 화면용 목록을 반환합니다. 로그인한 사용자만 호출 가능합니다.
            - sortType=LATEST: 최신 등록일 기준 내림차순 (기본값)
            - sortType=POPULAR: 최근 7일 투표 완료 수(배치 스냅샷) 기준 내림차순
            - categoryCodes 미지정 시: 사용자 관심 주제와 일치하는 카테고리만 상시 필터링됩니다.
            - categoryCodes 지정 시: 전달된 주제 코드 목록으로 필터링합니다.
            - 각 항목은 AI 헤드라인(`headline`)과 요약 본문(`content`)을 포함합니다.
            """)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MainAgendaSliceResponse.class),
                            examples = @ExampleObject(
                                    name = "조회 성공",
                                    value = AgendaApiExamples.EXAMPLE_GET_MAIN_AGENDAS_OK
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요 (JWT 누락/만료/위조)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/main")
    ResponseEntity<SliceResponse<MainAgendaResponse>> getMainAgendas(
            @Parameter(description = "주제 코드 목록(선택). 지정 시 해당 주제들만 조회", example = "DIGITAL,ECONOMY")
            @RequestParam(required = false) List<String> categoryCodes,
            @Parameter(
                    description = "정렬 방식 (LATEST: 최신 등록일순, POPULAR: 최근 7일 투표순)",
                    example = "LATEST",
                    schema = @Schema(
                            allowableValues = {"LATEST", "POPULAR"},
                            defaultValue = "LATEST"
                    )
            )
            @RequestParam(defaultValue = "LATEST")
            MainAgendaSortType sortType,
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "페이지 번호, 0부터 시작", example = "0")
            @RequestParam(defaultValue = "0")
            int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20")
            int size
    );

    @Operation(summary = "관심 주제 안건 목록 조회", description = """
            관심 주제 기반 안건 목록을 반환합니다. 로그인한 사용자만 호출 가능합니다.
            - sortType=LATEST: 최신 등록일 기준 내림차순 (기본값)
            - sortType=POPULAR: 최근 7일 투표 완료 수(배치 스냅샷) 기준 내림차순
            - 사용자 관심 주제와 일치하는 카테고리만 필터링됩니다.
            - 본 API는 조회수/투표수를 반환하지 않습니다.
            - 각 항목은 AI 헤드라인(`headline`)과 요약 본문(`content`)을 포함합니다.
            """)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InterestAgendaSliceResponse.class),
                            examples = @ExampleObject(
                                    name = "조회 성공",
                                    value = AgendaApiExamples.EXAMPLE_GET_INTEREST_AGENDAS_OK
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요 (JWT 누락/만료/위조)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/interests")
    ResponseEntity<SliceResponse<InterestAgendaResponse>> getInterestAgendas(
            @Parameter(
                    description = "정렬 방식 (LATEST: 최신 등록일순, POPULAR: 최근 7일 투표순)",
                    example = "LATEST",
                    schema = @Schema(
                            allowableValues = {"LATEST", "POPULAR"},
                            defaultValue = "LATEST"
                    )
            )
            @RequestParam(defaultValue = "LATEST")
            MainAgendaSortType sortType,
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "페이지 번호, 0부터 시작", example = "0")
            @RequestParam(defaultValue = "0")
            int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20")
            int size);

    @Operation(summary = "인기 안건 조회", description = """
            KST 월요일 00:00 기준 이번 주 조회 증가분 Top 5를 반환합니다. 로그인한 사용자만 호출 가능합니다.
            - TRENDING 탭(7일 투표 수)과 달리 **조회수** 기준이며, **주간(월~일)** 구간입니다.
            - 약 10분 주기 배치로 선계산된 `bill_popular_view_ranking` 스냅샷을 조회합니다.
            - 카테고리 필터 없이 전체 의안 대상입니다.
            - `previousRank`: 직전 배치 실행 시점의 순위 (없으면 null)
            - `rankChangeSteps`: 직전 배치 대비 순위 변동 단계 수 (상승=양수, 하락=음수, 유지/신규=0)
            - `rankChangeType`: 순위 변동 유형 (UP, DOWN, SAME, NEW)
            - 각 항목은 AI 헤드라인(`headline`)을 포함합니다.
            """)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PopularAgendaResponse.class)),
                            examples = @ExampleObject(
                                    name = "조회 성공",
                                    value = AgendaApiExamples.EXAMPLE_GET_POPULAR_AGENDAS_OK
                            ))),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요 (JWT 누락/만료/위조)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/popular")
    ResponseEntity<List<PopularAgendaResponse>> getPopularAgendas(
            @Parameter(hidden = true) Long userId);

    @Operation(summary = "의안 제목 검색", description = """
            입력한 키워드가 의안 제목에 포함된 안건 목록을 반환합니다.
            최신 등록일(proposalDate) 기준 내림차순으로 정렬됩니다.
            각 항목은 AI 헤드라인(`headline`)을 포함합니다.
            """)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = SearchAgendaSliceResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (JWT 누락/만료/위조)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search")
    ResponseEntity<SliceResponse<SearchAgendaResponse>> searchAgendas(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword,
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "페이지 번호, 0부터 시작", example = "0")
            @RequestParam(defaultValue = "0")
            int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20")
            int size);
}