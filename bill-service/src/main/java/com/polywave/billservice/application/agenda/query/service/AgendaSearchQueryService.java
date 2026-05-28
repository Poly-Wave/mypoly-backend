package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.api.dto.SearchAgendaResponse;
import com.polywave.billservice.api.dto.SliceResponse;
import org.springframework.data.domain.Pageable;

public interface AgendaSearchQueryService {
    SliceResponse<SearchAgendaResponse> searchAgendas(String keyword, Pageable pageable);
}
