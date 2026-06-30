package com.polywave.billservice.application.vote;

import com.polywave.billservice.application.vote.command.service.UserBillVoteCommand;
import com.polywave.billservice.application.vote.command.service.UserBillVoteCommandService;
import com.polywave.billservice.application.vote.query.service.UserBillVoteQueryService;
import com.polywave.billservice.client.UserServiceClient;
import com.polywave.billservice.common.exception.InvalidUserBirthDateException;
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
        var profile = userServiceClient.getMyProfile(command.userId());
        String voterAgeBand = AgeBand.tryFromBirthDate(profile.birthDate())
                .orElseThrow(InvalidUserBirthDateException::new)
                .name();
        String voterGender = normalizeGender(profile.gender());

        Long existingVoteId = userBillVoteQueryService
                .findVoteId(command.userId(), command.billId())
                .orElse(null);

        userBillVoteCommandService.voteOnBill(
                command.userId(),
                command.billId(),
                command.voteResult().name(),
                voterAgeBand,
                voterGender,
                existingVoteId
        );
    }

    private static String normalizeGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return null;
        }
        return gender.trim().toUpperCase();
    }
}
