package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotDebateAgendaQueryService {

    private final AgendaQueryRepository agendaQueryRepository;

    /**
     * 쟁쟁한 안건 목록. |찬성% - 반대%|가 작은 순(찬반이 팽팽한 순).
     */
    public List<AgendaResult> getAgendas(Pageable pageable) {
        return agendaQueryRepository.findHotDebateAgendas(pageable);
    }
}
