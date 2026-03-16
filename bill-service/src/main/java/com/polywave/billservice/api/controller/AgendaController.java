package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.AgendaResponse;
import com.polywave.billservice.api.dto.AgendaTabResponse;
import com.polywave.billservice.api.spec.AgendaApi;
import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.application.agenda.query.service.HotDebateAgendaQueryService;
import com.polywave.billservice.application.agenda.query.service.TrendingAgendaQueryService;
import com.polywave.billservice.domain.agenda.AgendaTabType;
import com.polywave.security.annotation.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgendaController implements AgendaApi {

    private final HotDebateAgendaQueryService hotDebateAgendaQueryService;
    private final TrendingAgendaQueryService trendingAgendaQueryService;

    @Override
    public ResponseEntity<List<AgendaTabResponse>> getTabs() {
        List<AgendaTabResponse> tabs = java.util.Arrays.stream(AgendaTabType.values())
                .map(AgendaTabResponse::from)
                .toList();
        return ResponseEntity.ok(tabs);
    }

    @Override
    public ResponseEntity<List<AgendaResponse>> getAgendasByTab(
            String tabCode,
            @LoginUser Long userId,
            Pageable pageable) {
        List<AgendaResult> agendas = switch (tabCode) {
            case "hot_debate" -> hotDebateAgendaQueryService.getAgendas(pageable);
            case "trending" -> trendingAgendaQueryService.getAgendas(pageable);
            case "personalized" -> List.of(); // TODO 구현
            default -> throw new IllegalArgumentException("Unknown agenda tab code: " + tabCode);
        };
        List<AgendaResponse> response = agendas.stream()
                .map(AgendaResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
