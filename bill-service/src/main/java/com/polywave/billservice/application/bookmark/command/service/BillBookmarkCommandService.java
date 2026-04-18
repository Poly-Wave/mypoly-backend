package com.polywave.billservice.application.bookmark.command.service;

import com.polywave.billservice.common.exception.BillNotFoundException;
import com.polywave.billservice.domain.Bill;
import com.polywave.billservice.domain.UserBillBookmark;
import com.polywave.billservice.repository.command.BillCommandRepository;
import com.polywave.billservice.repository.command.UserBillBookmarkCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BillBookmarkCommandService {

    private final BillCommandRepository billCommandRepository;
    private final UserBillBookmarkCommandRepository userBillBookmarkCommandRepository;

    @Transactional
    public void bookmark(Long userId, Long billId) {
        if (userBillBookmarkCommandRepository.existsByUserIdAndBill_Id(userId, billId)) {
            return;
        }

        Bill bill = billCommandRepository.findById(billId)
                .orElseThrow(BillNotFoundException::new);

        try {
            userBillBookmarkCommandRepository.save(UserBillBookmark.create(userId, bill));
        } catch (DataIntegrityViolationException ignored) {
            // 동시에 같은 의안을 보관하는 요청이 들어온 경우 unique constraint 충돌이 날 수 있다.
            // 보관하기 API는 멱등 동작으로 처리하므로 이미 보관된 상태라면 성공으로 간주한다.
        }
    }

    @Transactional
    public void unbookmark(Long userId, Long billId) {
        if (!billCommandRepository.existsById(billId)) {
            throw new BillNotFoundException();
        }

        userBillBookmarkCommandRepository.deleteByUserIdAndBill_Id(userId, billId);
    }
}