package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AgendaQueryRepository {

    /**
     * 쟁쟁한 안건 목록: |찬성% - 반대%|가 작은 순(찬반이 팽팽한 순).
     * 최소 투표 수 이상인 의안만 포함.
     */
    List<AgendaResult> findHotDebateAgendas(Pageable pageable);

    /**
     * 요즘 핫한 안건 목록: 7일 내 투표 완료 수 배치 스냅샷 기준 내림차순.
     */
    List<AgendaResult> findTrendingAgendas(Pageable pageable);
}
