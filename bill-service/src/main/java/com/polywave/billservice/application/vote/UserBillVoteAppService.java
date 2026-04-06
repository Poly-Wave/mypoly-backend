package com.polywave.billservice.application.vote;

import com.polywave.billservice.application.vote.command.service.UserBillVoteCommand;
import com.polywave.billservice.application.vote.command.service.UserBillVoteCommandService;
import com.polywave.billservice.application.vote.query.service.UserBillVoteQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBillVoteAppService {

    private final UserBillVoteQueryService userBillVoteQueryService;
    private final UserBillVoteCommandService userBillVoteCommandService;

    @Transactional
    public void voteOnBill(UserBillVoteCommand command) {
        Long existingVoteId = userBillVoteQueryService
                .findVoteId(command.userId(), command.billId())
                .orElse(null);

        userBillVoteCommandService.voteOnBill(
                command.userId(),
                command.billId(),
                command.voteResult().name(),
                existingVoteId
        );
    }
}
