package com.polywave.billservice.application.vote.command.service;

import com.polywave.billservice.domain.UserVoteResult;

public record UserBillVoteCommand(
        Long userId,
        Long billId,
        UserVoteResult voteResult
) {
}
