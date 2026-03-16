package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 요즘 핫한 탭: 7일 내 투표 완료 수 배치 스냅샷 기준 정렬.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendingAgendaQueryService {

    private final AgendaQueryRepository agendaQueryRepository;

    public List<AgendaResult> getAgendas(Pageable pageable) {
        return agendaQueryRepository.findTrendingAgendas(pageable);
    }
}
