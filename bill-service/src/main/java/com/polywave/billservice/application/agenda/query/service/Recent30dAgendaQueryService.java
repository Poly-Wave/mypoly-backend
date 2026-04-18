package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.config.AgendaProperties;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Recent30dAgendaQueryService {

    private final AgendaQueryRepository agendaQueryRepository;
    private final AgendaProperties agendaProperties;

    public List<AgendaResult> getAgendas(Long userId, Pageable pageable) {
        return agendaQueryRepository.findRecent30dAgendas(
                userId,
                agendaProperties.hotDebate().minVoteCount(),
                pageable
        );
    }
}
