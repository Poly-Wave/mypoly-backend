package com.polywave.billservice.application.agenda.query.service;

import com.polywave.billservice.application.agenda.query.result.SearchAgendaResult;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface AgendaSearchQueryService {

    /**
     * 키워드로 안건을 검색한다.
     * hasNext 판정을 위해 repository 가 pageSize + 1 개를 반환하므로 호출자(controller) 가 그대로 SliceResponse 로 묶는다.
     * (다른 agenda query service 들과 동일한 List<Result> 반환 컨벤션)
     */
    List<SearchAgendaResult> searchAgendas(String keyword, Pageable pageable);
}
