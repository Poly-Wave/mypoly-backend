package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.MyVotedBillResponse;
import com.polywave.billservice.api.dto.MyVotedBillSortType;
import com.polywave.billservice.api.dto.SliceResponse;
import com.polywave.billservice.api.dto.UserBillVoteRequest;
import com.polywave.billservice.api.spec.UserBillVoteApi;
import com.polywave.billservice.application.vote.UserBillVoteAppService;
import com.polywave.billservice.application.vote.command.service.UserBillVoteCommand;
import com.polywave.billservice.application.vote.query.service.UserBillVoteQueryService;
import com.polywave.billservice.domain.UserVoteResult;
import com.polywave.security.annotation.LoginUser;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserBillVoteController implements UserBillVoteApi {

    private final UserBillVoteAppService userBillVoteAppService;
    private final UserBillVoteQueryService userBillVoteQueryService;

    @Override
    public ResponseEntity<Void> voteOnBill(Long billId, @LoginUser Long userId, UserBillVoteRequest request) {
        UserBillVoteCommand command = new UserBillVoteCommand(userId, billId, request.voteResult());
        userBillVoteAppService.voteOnBill(command);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<SliceResponse<MyVotedBillResponse>> getMyVotedBills(
            LocalDate proposalFromDate,
            LocalDate proposalToDate,
            LocalDate votedFromDate,
            LocalDate votedToDate,
            Set<UserVoteResult> voteResults,
            MyVotedBillSortType sortType,
            @LoginUser Long userId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                userBillVoteQueryService.getMyVotedBills(
                        userId,
                        proposalFromDate,
                        proposalToDate,
                        votedFromDate,
                        votedToDate,
                        voteResults,
                        sortType,
                        pageable
                )
        );
    }
}