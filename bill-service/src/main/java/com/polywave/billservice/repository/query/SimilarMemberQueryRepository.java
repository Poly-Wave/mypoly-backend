package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.member.query.result.SimilarMemberResult;
import java.util.List;

public interface SimilarMemberQueryRepository {

    /**
     * 로그인 사용자의 의안 투표와 국회의원 본회의 표결을 같은 의안 기준으로 비교해,
     * 입장(찬성/반대)이 일치한 비율이 높은 순으로 국회의원을 반환한다.
     *
     * @param userId           기준 사용자 ID
     * @param minComparedVotes 함께 표결한 의안 수 최소 임계치 (이 값 미만인 의원은 제외)
     * @param limit            반환할 최대 의원 수
     */
    List<SimilarMemberResult> findSimilarMembersByUserVotes(Long userId, int minComparedVotes, int limit);
}
