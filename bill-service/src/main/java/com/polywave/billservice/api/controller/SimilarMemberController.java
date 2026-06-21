package com.polywave.billservice.api.controller;

import com.polywave.billservice.api.dto.SimilarMemberResponse;
import com.polywave.billservice.api.spec.SimilarMemberApi;
import com.polywave.billservice.application.member.query.service.SimilarMemberQueryService;
import com.polywave.security.annotation.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimilarMemberController implements SimilarMemberApi {

    private final SimilarMemberQueryService similarMemberQueryService;

    @Override
    public ResponseEntity<List<SimilarMemberResponse>> getSimilarMembers(@LoginUser Long userId) {
        return ResponseEntity.ok(similarMemberQueryService.getSimilarMembers(userId));
    }
}
