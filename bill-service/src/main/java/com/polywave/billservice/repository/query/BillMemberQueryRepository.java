package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.member.query.result.BillMemberDetailResult;
import java.util.Optional;

public interface BillMemberQueryRepository {

    Optional<BillMemberDetailResult> findMemberDetailById(Long memberId);
}