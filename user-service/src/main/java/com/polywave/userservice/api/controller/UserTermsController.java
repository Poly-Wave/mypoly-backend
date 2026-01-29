package com.polywave.userservice.api.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.UserAgreementRequest;
import com.polywave.userservice.application.userTerms.command.TermsAgreement;
import com.polywave.userservice.application.userTerms.command.UserAgreementCommand;
import com.polywave.userservice.application.userTerms.command.service.UserTermsCommandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user-terms")
public class UserTermsController {

    private final UserTermsCommandService userTermsCommandService;

    @PostMapping("/agree")
    public ResponseEntity<ApiResponse<Void>> agreeToTerms(@RequestBody UserAgreementRequest request) {

        List<TermsAgreement> termAgreements = request.termAgreements().stream()
                .map(ta -> new TermsAgreement(ta.termId(), ta.agreed()))
                .toList();

        UserAgreementCommand command = new UserAgreementCommand(request.userId(), termAgreements);

        userTermsCommandService.saveUserAgreement(command);

        return ResponseEntity.status(CREATED)
                .body(ApiResponse.ok("약관 동의 저장 성공"));
    }
}
