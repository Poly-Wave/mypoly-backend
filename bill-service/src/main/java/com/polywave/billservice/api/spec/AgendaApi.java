package com.polywave.billservice.api.spec;

import com.polywave.billservice.api.dto.AgendaResponse;
import com.polywave.billservice.api.dto.AgendaTabResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Agenda", description = "안건 탭 및 탭별 안건 목록 API (로그인 필요)")
@RequestMapping("/agendas")
public interface AgendaApi {

    @Operation(summary = "탭 목록 조회", description = "안건 목록에 사용할 탭(쟁쟁한, 맞춤형, 요즘 핫한 등) 메타 정보를 반환합니다. 로그인한 사용자만 호출 가능합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (JWT 누락/만료/위조)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/tabs")
    ResponseEntity<List<AgendaTabResponse>> getTabs();

    @Operation(summary = "탭별 안건 목록 조회", description = """
            탭 코드에 해당하는 안건 목록을 반환합니다. 로그인한 사용자만 호출 가능합니다.
            - hot_debate: 쟁쟁한 (찬반 비율이 팽팽한 순)
            - personalized: 맞춤형 (준비 중)
            - trending: 요즘 핫한 (최근 7일 투표 완료 수 순, 배치 선계산)
            """)
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "알 수 없는 탭 코드",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (JWT 누락/만료/위조)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/tabs/{tabCode}")
    ResponseEntity<List<AgendaResponse>> getAgendasByTab(
            @Parameter(description = "탭 코드 (hot_debate, personalized, trending)", required = true)
            @PathVariable String tabCode,
            @Parameter(hidden = true) Long userId,
            Pageable pageable
    );
}
