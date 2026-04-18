package com.polywave.billservice.application.bill;

import com.polywave.billservice.api.dto.BillDetailResponse;
import com.polywave.billservice.application.bill.query.service.BillDetailQueryService;
import com.polywave.billservice.application.view.command.service.BillViewCountCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BillDetailAppService {

    private final BillDetailQueryService billDetailQueryService;
    private final BillViewCountCommandService billViewCountCommandService;

    @Transactional
    public BillDetailResponse getBillDetail(Long billId, Long userId) {
        BillDetailResponse response = billDetailQueryService.getBillDetail(billId, userId);

        boolean viewCountIncreased = billViewCountCommandService.increaseViewCountIfCountable(userId, billId);
        if (!viewCountIncreased) {
            return response;
        }

        return response.withViewCount(response.viewCount() + 1);
    }
}