package com.polywave.billservice.application.view.command.service;

import com.polywave.billservice.repository.command.BillCommandRepository;
import com.polywave.billservice.repository.command.UserBillViewCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BillViewCountCommandService {

    private final UserBillViewCommandRepository userBillViewCommandRepository;
    private final BillCommandRepository billCommandRepository;

    @Transactional
    public boolean increaseViewCountIfCountable(Long userId, Long billId) {
        int recorded = userBillViewCommandRepository.upsertIfViewCountable(userId, billId);
        if (recorded == 0) {
            return false;
        }

        billCommandRepository.increaseViewCount(billId);
        return true;
    }
}