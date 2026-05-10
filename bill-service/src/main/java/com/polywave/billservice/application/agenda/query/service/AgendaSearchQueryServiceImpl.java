package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.application.agenda.query.result.SearchAgendaResult;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgendaSearchQueryServiceImpl implements AgendaSearchQueryService {

    private final AgendaQueryRepository agendaQueryRepository;

    @Override
    public List<SearchAgendaResult> searchAgendas(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        return agendaQueryRepository.searchAgendasByTitle(keyword, pageable);
    }
}
