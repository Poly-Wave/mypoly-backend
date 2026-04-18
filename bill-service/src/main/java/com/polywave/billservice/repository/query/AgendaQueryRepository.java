package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.agenda.query.result.AgendaResult;
import com.polywave.billservice.application.agenda.query.result.MainAgendaResult;
import com.polywave.billservice.domain.AgeBand;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface AgendaQueryRepository {

    /**
     * 쟁쟁한 안건 목록: |찬성% - 반대%|가 작은 순(찬반이 팽팽한 순).
     * 최소 투표 수 이상인 의안만 포함.
     */
    List<AgendaResult> findHotDebateAgendas(Long userId, int days, int minVoteCount, Pageable pageable);

    /**
     * 요즘 핫한 안건 목록: 7일 내 투표 완료 수 배치 스냅샷 기준 내림차순.
     */
    List<AgendaResult> findTrendingAgendas(Long userId, Pageable pageable);

    /**
     * 최근 30일 내 등록 안건 중 이번 달 누적 투표 완료 수 내림차순.
     */
    List<AgendaResult> findRecent30dAgendas(Long userId, Pageable pageable);

    /**
     * 최근 7일 내 등록 안건 중 동일 연령대 투표 완료 수 내림차순.
     */
    List<AgendaResult> findSameAgeAgendas(Long userId, AgeBand ageBand, Pageable pageable);

    /**
     * 안건 메인 목록 조회.
     */
    List<MainAgendaResult> findMainAgendas(
            Long userId,
            boolean applyInterestFilter,
            Set<Long> interestCategoryIds,
            Pageable pageable
    );
}
