package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.BillMemberDetailResponse;
import com.polywave.billservice.api.spec.BillMemberApi;
import com.polywave.billservice.application.member.query.service.BillMemberQueryService;
import com.polywave.security.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BillMemberController implements BillMemberApi {

    private final BillMemberQueryService billMemberQueryService;

    @Override
    public ResponseEntity<BillMemberDetailResponse> getMemberDetail(Long memberId, @LoginUser Long userId) {
        return ResponseEntity.ok(billMemberQueryService.getMemberDetail(memberId, userId));
    }
}