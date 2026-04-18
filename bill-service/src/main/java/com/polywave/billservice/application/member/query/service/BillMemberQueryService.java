package com.polywave.billservice.application.member.query.service;

import com.polywave.billservice.api.dto.BillMemberDetailResponse;
import com.polywave.billservice.application.member.query.result.BillMemberDetailResult;
import com.polywave.billservice.common.exception.BillMemberNotFoundException;
import com.polywave.billservice.repository.query.BillMemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillMemberQueryService {

    private final BillMemberQueryRepository billMemberQueryRepository;

    public BillMemberDetailResponse getMemberDetail(Long memberId) {
        BillMemberDetailResult result = billMemberQueryRepository.findMemberDetailById(memberId)
                .orElseThrow(BillMemberNotFoundException::new);

        return BillMemberDetailResponse.from(result);
    }
}