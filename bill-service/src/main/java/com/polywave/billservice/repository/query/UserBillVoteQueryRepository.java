package com.polywave.billservice.repository.query;

import com.polywave.billservice.api.dto.MyVotedBillSortType;
import com.polywave.billservice.application.vote.query.result.MyVotedBillResult;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;

public interface UserBillVoteQueryRepository {

    Optional<Long> findVoteIdByUserIdAndBillId(Long userId, Long billId);

    List<MyVotedBillResult> findMyVotedBills(
            Long userId,
            LocalDate proposalFromDate,
            LocalDate proposalToDate,
            Instant fromVotedAt,
            Instant toVotedAtExclusive,
            Set<String> voteResults,
            MyVotedBillSortType sortType,
            Pageable pageable
    );
}