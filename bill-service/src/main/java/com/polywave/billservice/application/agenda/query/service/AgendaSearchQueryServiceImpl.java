package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.api.dto.SearchAgendaResponse;
import com.polywave.billservice.api.dto.SliceResponse;
import com.polywave.billservice.application.agenda.query.result.SearchAgendaResult;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgendaSearchQueryServiceImpl implements AgendaSearchQueryService {

    private final AgendaQueryRepository agendaQueryRepository;

    @Override
    public SliceResponse<SearchAgendaResponse> searchAgendas(String keyword, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        if (keyword == null || keyword.trim().isEmpty()) {
            return SliceResponse.of(List.of(), pageable.getPageNumber(), pageSize, false);
        }

        List<SearchAgendaResult> results = agendaQueryRepository.searchAgendasByTitle(keyword, pageable);
        boolean hasNext = results.size() > pageSize;
        List<SearchAgendaResponse> content = results.stream()
                .limit(pageSize)
                .map(SearchAgendaResponse::from)
                .toList();

        return SliceResponse.of(content, pageable.getPageNumber(), pageSize, hasNext);
    }
}
