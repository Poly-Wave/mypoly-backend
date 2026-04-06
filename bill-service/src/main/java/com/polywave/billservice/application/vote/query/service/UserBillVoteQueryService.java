package com.polywave.billservice.application.vote.query.service;

import com.polywave.billservice.repository.query.UserBillVoteQueryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserBillVoteQueryService {

    private final UserBillVoteQueryRepository userBillVoteQueryRepository;

    public Optional<Long> findVoteId(Long userId, Long billId) {
        return userBillVoteQueryRepository.findVoteIdByUserIdAndBillId(userId, billId);
    }
}
