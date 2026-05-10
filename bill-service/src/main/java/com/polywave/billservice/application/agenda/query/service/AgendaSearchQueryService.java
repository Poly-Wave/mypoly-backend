package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.application.agenda.query.result.SearchAgendaResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AgendaSearchQueryService {
    List<SearchAgendaResult> searchAgendas(String keyword, Pageable pageable);
}
