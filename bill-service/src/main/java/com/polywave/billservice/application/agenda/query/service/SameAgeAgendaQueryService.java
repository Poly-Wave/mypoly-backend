package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.client.UserServiceClient;
import com.polywave.billservice.domain.AgeBand;
import com.polywave.billservice.repository.query.AgendaQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SameAgeAgendaQueryService {

    private final AgendaQueryRepository agendaQueryRepository;
    private final UserServiceClient userServiceClient;

    public List<AgendaResult> getAgendas(Long userId, Pageable pageable) {
        String birthDate = userServiceClient.getMyBirthDate(userId);
        AgeBand ageBand = AgeBand.fromBirthDate(birthDate);
        return agendaQueryRepository.findSameAgeAgendas(userId, ageBand, pageable);
    }
}
