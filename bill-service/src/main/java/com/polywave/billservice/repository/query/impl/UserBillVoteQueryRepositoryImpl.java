package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.domain.QUserBillVote;
import com.polywave.billservice.repository.query.UserBillVoteQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserBillVoteQueryRepositoryImpl implements UserBillVoteQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Long> findVoteIdByUserIdAndBillId(Long userId, Long billId) {
        QUserBillVote userBillVote = QUserBillVote.userBillVote;
        Long voteId = queryFactory
                .select(userBillVote.id)
                .from(userBillVote)
                .where(userBillVote.userId.eq(userId).and(userBillVote.bill.id.eq(billId)))
                .fetchOne();
        return Optional.ofNullable(voteId);
    }
}
