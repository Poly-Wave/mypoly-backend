package com.polywave.billservice.repository.query;

import java.util.Optional;

public interface UserBillVoteQueryRepository {
    Optional<Long> findVoteIdByUserIdAndBillId(Long userId, Long billId);
}
