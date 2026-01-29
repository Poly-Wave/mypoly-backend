package com.polywave.userservice.application.userTerms.command.service;

import com.polywave.userservice.application.userTerms.command.TermsAgreement;
import com.polywave.userservice.application.userTerms.command.UserAgreementCommand;
import com.polywave.userservice.domain.Terms;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserTerms;
import com.polywave.userservice.repository.command.UserTermsCommandRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTermsCommandService {

    private final UserTermsCommandRepository userTermsCommandRepository;
    private final EntityManager entityManager;

    @Transactional
    public void saveUserAgreement(UserAgreementCommand command) {
        Long userId = command.userId();
        List<TermsAgreement> termAgreements = command.termsAgreements();

        if (termAgreements == null || termAgreements.isEmpty()) {
            return; // 처리할 약관이 없으면 종료
        }

        // User Proxy 생성 (DB 조회 안 함)
        User userProxy = entityManager.getReference(User.class, userId);

        // Terms Proxy 생성 (DB 조회 안 함)
        List<UserTerms> userTermsToSave = termAgreements.stream()
                .map(ta -> {
                    Terms termsProxy = entityManager.getReference(Terms.class, ta.termId());
                    return UserTerms.builder()
                            .user(userProxy)
                            .terms(termsProxy)
                            .agreedAt(Instant.now())  // UTC 기준 시간
                            .agreed(ta.agreed())      // Term별 동의 여부 적용
                            .build();
                })
                .toList();

        // 저장
        userTermsCommandRepository.saveAll(userTermsToSave);
    }
}
