package com.polywave.billservice.application.member.query.service;

import com.polywave.billservice.api.dto.BillMemberDetailResponse;
import com.polywave.billservice.application.member.query.result.BillMemberDetailResult;
import com.polywave.billservice.application.member.query.result.BillMemberRepresentativeBillResult;
import com.polywave.billservice.application.member.query.result.MemberInterestAffinityResult;
import com.polywave.billservice.common.exception.BillMemberNotFoundException;
import com.polywave.billservice.repository.query.BillMemberQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillMemberQueryService {

    private static final int REPRESENTATIVE_BILL_LIMIT = 10;

    private final BillMemberQueryRepository billMemberQueryRepository;
    private final MemberInterestAffinityQueryService memberInterestAffinityQueryService;

    public BillMemberDetailResponse getMemberDetail(Long memberId, Long userId) {
        BillMemberDetailResult memberDetail = billMemberQueryRepository.findMemberDetailById(memberId)
                .orElseThrow(BillMemberNotFoundException::new);

        List<BillMemberRepresentativeBillResult> representativeBills =
                billMemberQueryRepository.findRepresentativeBillsByMemberId(memberId, REPRESENTATIVE_BILL_LIMIT);

        MemberInterestAffinityResult interestAffinity =
                memberInterestAffinityQueryService.getInterestAffinity(memberId, userId);

        return BillMemberDetailResponse.from(memberDetail, representativeBills, interestAffinity);
    }
}
