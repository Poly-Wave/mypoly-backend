package com.polywave.billservice.application.member.query.service;

import com.polywave.billservice.api.dto.SimilarMemberResponse;
import com.polywave.billservice.application.member.query.result.SimilarMemberResult;
import com.polywave.billservice.repository.query.SimilarMemberQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimilarMemberQueryService {

    // 유사도를 산정하기 위해 사용자와 의원이 함께 표결해야 하는 최소 의안 수.
    private static final int MIN_COMPARED_VOTES = 10;

    // 반환할 유사 의원 최대 수.
    private static final int SIMILAR_MEMBER_LIMIT = 20;

    private final SimilarMemberQueryRepository similarMemberQueryRepository;

    public List<SimilarMemberResponse> getSimilarMembers(Long userId) {
        List<SimilarMemberResult> results = similarMemberQueryRepository
                .findSimilarMembersByUserVotes(userId, MIN_COMPARED_VOTES, SIMILAR_MEMBER_LIMIT);

        return results.stream()
                .map(SimilarMemberResponse::from)
                .toList();
    }
}
