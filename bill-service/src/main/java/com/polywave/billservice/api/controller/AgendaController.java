package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.AgendaResponse;
import com.polywave.billservice.api.dto.AgendaTabResponse;
import com.polywave.billservice.api.spec.AgendaApi;
import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.application.agenda.query.service.AgendaTabQueryService;
import com.polywave.billservice.application.agenda.query.service.HotDebateAgendaQueryService;
import com.polywave.billservice.application.agenda.query.service.TrendingAgendaQueryService;
import com.polywave.billservice.common.exception.InvalidAgendaTabCodeException;
import com.polywave.security.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgendaController implements AgendaApi {

    private final AgendaTabQueryService agendaTabQueryService;
    private final HotDebateAgendaQueryService hotDebateAgendaQueryService;
    private final TrendingAgendaQueryService trendingAgendaQueryService;

    @Override
    public ResponseEntity<List<AgendaTabResponse>> getTabs() {
        List<AgendaTabResponse> tabs = agendaTabQueryService.getActiveTabs().stream()
                .map(AgendaTabResponse::from)
                .toList();
        return ResponseEntity.ok(tabs);
    }

    @Operation(summary = "탭별 안건 목록 조회", description = """
            탭 코드에 해당하는 안건 목록을 반환합니다. 로그인한 사용자만 호출 가능합니다.
            - HOT_DEBATE: 쟁쟁한 (찬반 비율이 팽팽한 순)
            - PERSONALIZED: 맞춤형 (준비 중)
            - TRENDING: 요즘 핫한 (최근 7일 투표 완료 수 순, 배치 선계산)
            """)
    @Override
    public ResponseEntity<List<AgendaResponse>> getAgendasByTab(
            String tabCode,
            @LoginUser Long userId,
            Pageable pageable) {
        String normalizedTabCode = tabCode.toUpperCase(Locale.ROOT);
        List<AgendaResult> agendas = switch (normalizedTabCode) {
            case "HOT_DEBATE" -> hotDebateAgendaQueryService.getAgendas(userId, pageable);
            case "TRENDING" -> trendingAgendaQueryService.getAgendas(userId, pageable);
            case "PERSONALIZED" -> List.of(); // TODO 구현
            default -> throw new InvalidAgendaTabCodeException();
        };

        List<AgendaResponse> response = agendas.stream()
                .map(AgendaResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}