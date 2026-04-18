package com.polywave.billservice.application.vote;

import com.polywave.billservice.application.vote.command.service.UserBillVoteCommand;
import com.polywave.billservice.application.vote.command.service.UserBillVoteCommandService;
import com.polywave.billservice.application.vote.query.service.UserBillVoteQueryService;
import com.polywave.billservice.client.UserServiceClient;
import com.polywave.billservice.domain.AgeBand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBillVoteAppService {

    private final UserBillVoteQueryService userBillVoteQueryService;
    private final UserBillVoteCommandService userBillVoteCommandService;
    private final UserServiceClient userServiceClient;

    @Transactional
    public void voteOnBill(UserBillVoteCommand command) {
        String birthDate = userServiceClient.getMyBirthDate(command.userId());
        String voterAgeBand = AgeBand.fromBirthDate(birthDate).name();

        Long existingVoteId = userBillVoteQueryService
                .findVoteId(command.userId(), command.billId())
                .orElse(null);

        userBillVoteCommandService.voteOnBill(
                command.userId(),
                command.billId(),
                command.voteResult().name(),
                voterAgeBand,
                existingVoteId
        );
    }
}
