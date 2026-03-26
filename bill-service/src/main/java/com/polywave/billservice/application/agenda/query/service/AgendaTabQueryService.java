package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.domain.agenda.AgendaTab;
import com.polywave.billservice.repository.query.AgendaTabQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgendaTabQueryService {

    private final AgendaTabQueryRepository agendaTabQueryRepository;

    public List<AgendaTab> getActiveTabs() {
        return agendaTabQueryRepository.findActiveOrderByDisplayOrder();
    }
}

