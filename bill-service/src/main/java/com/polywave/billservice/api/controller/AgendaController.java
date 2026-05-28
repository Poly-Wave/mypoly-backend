package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.AgendaResponse;
import com.polywave.billservice.api.dto.AgendaTabResponse;
import com.polywave.billservice.api.dto.InterestAgendaResponse;
import com.polywave.billservice.api.dto.MainAgendaSortType;
import com.polywave.billservice.api.dto.MainAgendaResponse;
import com.polywave.billservice.api.dto.PopularAgendaResponse;
import com.polywave.billservice.api.spec.AgendaApi;
import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.application.agenda.query.result.MainAgendaResult;
import com.polywave.billservice.application.agenda.query.result.PopularAgendaResult;
import com.polywave.billservice.api.dto.SearchAgendaResponse;
import com.polywave.billservice.api.dto.SliceResponse;
import com.polywave.billservice.application.agenda.query.service.AgendaSearchQueryService;
import com.polywave.billservice.application.agenda.query.service.AgendaTabQueryService;
import com.polywave.billservice.application.agenda.query.service.HotDebateAgendaQueryService;
import com.polywave.billservice.application.agenda.query.service.MainAgendaQueryService;
import com.polywave.billservice.application.agenda.query.service.PopularAgendaQueryService;
import com.polywave.billservice.application.agenda.query.service.Recent30dAgendaQueryService;
import com.polywave.billservice.application.agenda.query.service.SameAgeAgendaQueryService;
import com.polywave.billservice.application.agenda.query.service.TrendingAgendaQueryService;
import com.polywave.billservice.common.exception.InvalidAgendaTabCodeException;
import com.polywave.security.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AgendaController implements AgendaApi {

    private final AgendaTabQueryService agendaTabQueryService;
    private final HotDebateAgendaQueryService hotDebateAgendaQueryService;
    private final TrendingAgendaQueryService trendingAgendaQueryService;
    private final Recent30dAgendaQueryService recent30dAgendaQueryService;
    private final SameAgeAgendaQueryService sameAgeAgendaQueryService;
    private final MainAgendaQueryService mainAgendaQueryService;
    private final AgendaSearchQueryService agendaSearchQueryService;
    private final PopularAgendaQueryService popularAgendaQueryService;

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
            - TRENDING: 요즘 핫한 (최근 7일 투표 완료 수 순, 배치 선계산)
            - RECENT_30D: 최근 30일 (최근 30일 이내 투표가 최소 M건 이상인 의안만, M은 쟁쟁한과 동일, 해당 기간 투표 수 많은 순)
            - SAME_AGE: 내 또래 (최근 7일 이내 동일 연령대 투표만 집계, 10건 미만 의안 제외. 일수·최소 투표 수는 쟁쟁한과 동일 설정)
            """)
    @Override
    public ResponseEntity<SliceResponse<AgendaResponse>> getAgendasByTab(
            String tabCode,
            @LoginUser Long userId,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        String normalizedTabCode = tabCode.toUpperCase(Locale.ROOT);
        List<AgendaResult> agendas = switch (normalizedTabCode) {
            case "HOT_DEBATE" -> hotDebateAgendaQueryService.getAgendas(userId, pageable);
            case "TRENDING" -> trendingAgendaQueryService.getAgendas(userId, pageable);
            case "RECENT_30D" -> recent30dAgendaQueryService.getAgendas(userId, pageable);
            case "SAME_AGE" -> sameAgeAgendaQueryService.getAgendas(userId, pageable);
            default -> throw new InvalidAgendaTabCodeException();
        };

        return ResponseEntity.ok(toSliceResponse(agendas, pageable, AgendaResponse::from));
    }

    @Override
    public ResponseEntity<SliceResponse<MainAgendaResponse>> getMainAgendas(
            List<String> categoryCodes,
            MainAgendaSortType sortType,
            @LoginUser Long userId,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<MainAgendaResult> results = mainAgendaQueryService.getMainAgendas(userId, categoryCodes, sortType, pageable);

        return ResponseEntity.ok(toSliceResponse(results, pageable, MainAgendaResponse::from));
    }

    @Override
    public ResponseEntity<SliceResponse<InterestAgendaResponse>> getInterestAgendas(
            MainAgendaSortType sortType,
            @LoginUser Long userId,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<MainAgendaResult> results = mainAgendaQueryService.getInterestAgendas(userId, sortType, pageable);

        return ResponseEntity.ok(toSliceResponse(results, pageable, InterestAgendaResponse::from));
    }

    @Override
    public ResponseEntity<List<PopularAgendaResponse>> getPopularAgendas(@LoginUser Long userId) {
        List<PopularAgendaResult> results = popularAgendaQueryService.getPopularAgendas(userId);
        List<PopularAgendaResponse> response = results.stream()
                .map(PopularAgendaResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SliceResponse<SearchAgendaResponse>> searchAgendas(
            String keyword,
            @LoginUser Long userId,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(agendaSearchQueryService.searchAgendas(keyword, pageable));
    }

    private static <T, R> SliceResponse<R> toSliceResponse(
            List<T> results,
            Pageable pageable,
            Function<T, R> mapper) {
        int pageSize = pageable.getPageSize();
        boolean hasNext = results.size() > pageSize;
        List<R> content = results.stream()
                .limit(pageSize)
                .map(mapper)
                .toList();

        return SliceResponse.of(content, pageable.getPageNumber(), pageSize, hasNext);
    }
}