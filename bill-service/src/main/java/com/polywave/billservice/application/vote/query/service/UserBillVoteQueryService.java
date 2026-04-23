package com.polywave.billservice.application.vote.query.service;

import com.polywave.billservice.api.dto.MyVotedBillResponse;
import com.polywave.billservice.api.dto.SliceResponse;
import com.polywave.billservice.application.vote.query.result.MyVotedBillResult;
import com.polywave.billservice.domain.UserVoteResult;
import com.polywave.billservice.repository.query.UserBillVoteQueryRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserBillVoteQueryService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final UserBillVoteQueryRepository userBillVoteQueryRepository;

    public Optional<Long> findVoteId(Long userId, Long billId) {
        return userBillVoteQueryRepository.findVoteIdByUserIdAndBillId(userId, billId);
    }

    public SliceResponse<MyVotedBillResponse> getMyVotedBills(
            Long userId,
            LocalDate fromDate,
            LocalDate toDate,
            Set<UserVoteResult> voteResults,
            Pageable pageable
    ) {
        Instant fromVotedAt = toStartOfDayInstant(fromDate);
        Instant toVotedAtExclusive = toExclusiveEndInstant(toDate);
        Set<String> normalizedVoteResults = normalizeVoteResults(voteResults);

        List<MyVotedBillResult> results = userBillVoteQueryRepository.findMyVotedBills(
                userId,
                fromVotedAt,
                toVotedAtExclusive,
                normalizedVoteResults,
                pageable
        );

        int pageSize = pageable.getPageSize();
        boolean hasNext = results.size() > pageSize;
        List<MyVotedBillResponse> content = results.stream()
                .limit(pageSize)
                .map(MyVotedBillResponse::from)
                .toList();

        return SliceResponse.of(
                content,
                pageable.getPageNumber(),
                pageSize,
                hasNext
        );
    }

    private Instant toStartOfDayInstant(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(KST).toInstant();
    }

    private Instant toExclusiveEndInstant(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.plusDays(1).atStartOfDay(KST).toInstant();
    }

    private Set<String> normalizeVoteResults(Set<UserVoteResult> voteResults) {
        if (voteResults == null || voteResults.isEmpty()) {
            return Set.of();
        }

        return voteResults.stream()
                .map(Enum::name)
                .collect(Collectors.toUnmodifiableSet());
    }
}