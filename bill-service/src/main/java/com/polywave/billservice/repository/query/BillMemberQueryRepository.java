package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.member.query.result.BillMemberDetailResult;
import com.polywave.billservice.application.member.query.result.BillMemberRepresentativeBillResult;
import java.util.List;
import java.util.Optional;

public interface BillMemberQueryRepository {

    Optional<BillMemberDetailResult> findMemberDetailById(Long memberId);

    List<BillMemberRepresentativeBillResult> findRepresentativeBillsByMemberId(Long memberId, int limit);
}