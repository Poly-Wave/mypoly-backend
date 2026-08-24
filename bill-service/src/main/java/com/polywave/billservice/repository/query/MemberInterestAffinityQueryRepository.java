package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.member.query.result.CategoryCountResult;
import java.util.List;

public interface MemberInterestAffinityQueryRepository {

    /** 의원이 발의(대표 + 공동)한 의안을 대표 카테고리 기준으로 집계한다. */
    List<CategoryCountResult> findMemberProposalCountsByCategory(Long memberId);

    /** 사용자가 투표한 의안을 대표 카테고리 기준으로 집계한다. */
    List<CategoryCountResult> findUserVoteCountsByCategory(Long userId);
}
