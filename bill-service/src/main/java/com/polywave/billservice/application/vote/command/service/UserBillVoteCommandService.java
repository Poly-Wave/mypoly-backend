package com.polywave.billservice.application.vote.command.service;

import com.polywave.billservice.common.exception.BillNotFoundException;
import com.polywave.billservice.domain.Bill;
import com.polywave.billservice.domain.UserBillVote;
import com.polywave.billservice.repository.command.BillCommandRepository;
import com.polywave.billservice.repository.command.UserBillVoteCommandRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBillVoteCommandService {

    private final BillCommandRepository billCommandRepository;
    private final UserBillVoteCommandRepository userBillVoteCommandRepository;

    @Transactional
    public void voteOnBill(Long userId, Long billId, String voteResult, String voterAgeBand, String voterGender, Long existingVoteId) {
        Instant now = Instant.now();
        if (existingVoteId == null) {
            Bill bill = billCommandRepository.findById(billId)
                    .orElseThrow(BillNotFoundException::new);
            UserBillVote newVote = UserBillVote.create(userId, bill, voteResult, voterAgeBand, voterGender, now);
            userBillVoteCommandRepository.save(newVote);
            return;
        }

        UserBillVote existingVote = userBillVoteCommandRepository.findById(existingVoteId)
                .orElseThrow(() -> new IllegalStateException("User vote not found for update"));
        existingVote.changeVote(voteResult, voterAgeBand, voterGender, now);
    }
}
