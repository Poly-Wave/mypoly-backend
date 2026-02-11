package com.polywave.userservice.api.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.polywave.security.annotation.LoginUser;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.TermsAgreementRequest;
import com.polywave.userservice.api.dto.UserAgreementRequest;
import com.polywave.userservice.api.spec.UserTermsApi;
import com.polywave.userservice.application.userterms.command.TermsAgreement;
import com.polywave.userservice.application.userterms.command.UserAgreementCommand;
import com.polywave.userservice.application.userterms.command.service.UserTermsCommandService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-terms")
public class UserTermsController implements UserTermsApi {

        private final UserTermsCommandService userTermsCommandService;

        @Override
        public ResponseEntity<ApiResponse<Void>> agreeToTerms(
                        UserAgreementRequest request,
                        @LoginUser Long userId) {
                // 중복 termId 방지 (DB 유니크 충돌 방지)
                List<Long> duplicates = findDuplicateTermIds(request.termAgreements());
                if (!duplicates.isEmpty()) {
                        throw new IllegalArgumentException("중복된 약관 ID(termId)가 포함되어 있습니다: " + duplicates);
                }

                List<TermsAgreement> termAgreements = request.termAgreements().stream()
                                .map(ta -> new TermsAgreement(ta.termId(), ta.agreed()))
                                .toList();

                UserAgreementCommand command = new UserAgreementCommand(userId, termAgreements);
                userTermsCommandService.saveUserAgreement(command);

                return ResponseEntity.status(CREATED)
                                .body(ApiResponse.ok("약관 동의 저장 성공"));
        }

        private List<Long> findDuplicateTermIds(List<TermsAgreementRequest> agreements) {
                Set<Long> seen = new HashSet<>();
                List<Long> duplicates = new ArrayList<>();

                for (TermsAgreementRequest a : agreements) {
                        Long termId = a.termId(); // @Valid/@NotNull로 null은 여기까지 안 오는 게 정상
                        if (!seen.add(termId)) {
                                duplicates.add(termId);
                        }
                }

                // 중복 목록은 중복 제거 + 정렬해서 메시지 안정화
                return duplicates.stream().distinct().sorted().toList();
        }
}
