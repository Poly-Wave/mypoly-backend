package com.polywave.billservice.repository.query;

import com.polywave.billservice.domain.agenda.AgendaTab;
import java.util.List;

public interface AgendaTabQueryRepository {

    List<AgendaTab> findActiveOrderByDisplayOrder();
}

