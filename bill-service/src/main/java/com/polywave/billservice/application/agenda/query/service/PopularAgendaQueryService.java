package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.application.agenda.query.result.PopularAgendaResult;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularAgendaQueryService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final AgendaQueryRepository agendaQueryRepository;

    public List<PopularAgendaResult> getPopularAgendas(Long userId) {
        return agendaQueryRepository.findPopularAgendas(userId, currentWeekStartKst());
    }

    private LocalDate currentWeekStartKst() {
        return LocalDate.now(KST).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}
