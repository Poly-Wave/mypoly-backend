package com.polywave.billservice.repository.command;

import com.polywave.billservice.domain.UserBillVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBillVoteCommandRepository extends JpaRepository<UserBillVote, Long> {
}
