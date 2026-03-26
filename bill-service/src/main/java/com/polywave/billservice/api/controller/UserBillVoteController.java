package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.UserBillVoteRequest;
import com.polywave.billservice.api.spec.UserBillVoteApi;
import com.polywave.billservice.application.vote.UserBillVoteAppService;
import com.polywave.billservice.application.vote.command.service.UserBillVoteCommand;
import com.polywave.security.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserBillVoteController implements UserBillVoteApi {

    private final UserBillVoteAppService userBillVoteAppService;

    @Override
    public ResponseEntity<Void> voteOnBill(Long billId, @LoginUser Long userId, UserBillVoteRequest request) {
        UserBillVoteCommand command = new UserBillVoteCommand(userId, billId, request.voteResult());
        userBillVoteAppService.voteOnBill(command);
        return ResponseEntity.ok().build();
    }
}
