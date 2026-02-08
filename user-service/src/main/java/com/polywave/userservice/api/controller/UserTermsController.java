package com.polywave.userservice.api.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.polywave.security.annotation.LoginUser;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.UserAgreementRequest;
import com.polywave.userservice.application.userTerms.command.TermsAgreement;
import com.polywave.userservice.application.userTerms.command.UserAgreementCommand;
import com.polywave.userservice.application.userTerms.command.service.UserTermsCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "UserTerms", description = "유저 약관 동의 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-terms")
public class UserTermsController {

    private final UserTermsCommandService userTermsCommandService;

    @Operation(summary = "약관 동의 저장", description = "JWT 인증이 필요합니다. Authorize에 토큰 입력 후 호출하세요.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/agree")
    public ResponseEntity<ApiResponse<Void>> agreeToTerms(
            @Valid @RequestBody UserAgreementRequest request,
            @LoginUser Long userId
    ) {
        List<TermsAgreement> termAgreements = request.termAgreements().stream()
                .map(ta -> new TermsAgreement(ta.termId(), ta.agreed()))
                .toList();

        UserAgreementCommand command = new UserAgreementCommand(userId, termAgreements);
        userTermsCommandService.saveUserAgreement(command);

        return ResponseEntity.status(CREATED)
                .body(ApiResponse.ok("약관 동의 저장 성공"));
    }
}
