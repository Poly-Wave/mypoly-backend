package com.polywave.userservice.application.userTerms.command.service;

import com.polywave.userservice.application.userTerms.command.TermsAgreement;
import com.polywave.userservice.application.userTerms.command.UserAgreementCommand;
import com.polywave.userservice.domain.Terms;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserTerms;
import com.polywave.userservice.repository.command.UserTermsCommandRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTermsCommandService {

    private final UserTermsCommandRepository userTermsCommandRepository;
    private final EntityManager entityManager;

    /**
     * 업서트 방식:
     * - (user_id, terms_id) 유니크 보장
     * - 있으면 update(agreed/agreedAt), 없으면 insert
     */
    @Transactional
    public void saveUserAgreement(UserAgreementCommand command) {
        Long userId = command.userId();
        List<TermsAgreement> termAgreements = command.termsAgreements();

        if (termAgreements == null || termAgreements.isEmpty()) {
            return;
        }

        // termId 중복 제거
        List<Long> termIds = termAgreements.stream()
                .map(TermsAgreement::termId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (termIds.isEmpty()) {
            return;
        }

        // 기존 데이터 조회 (한방에)
        List<UserTerms> existing = userTermsCommandRepository.findByUserIdAndTermsIdIn(userId, termIds);
        Map<Long, UserTerms> existingMap = existing.stream()
                .collect(Collectors.toMap(
                        ut -> ut.getTerms().getId(),
                        Function.identity(),
                        (a, b) -> a
                ));

        User userProxy = entityManager.getReference(User.class, userId);
        Instant now = Instant.now();

        List<UserTerms> toInsert = new ArrayList<>();

        for (TermsAgreement ta : termAgreements) {
            if (ta == null || ta.termId() == null) continue;

            UserTerms current = existingMap.get(ta.termId());
            if (current != null) {
                current.updateAgreement(ta.agreed(), now);
                continue;
            }

            Terms termsProxy = entityManager.getReference(Terms.class, ta.termId());
            toInsert.add(UserTerms.builder()
                    .user(userProxy)
                    .terms(termsProxy)
                    .agreedAt(now)
                    .agreed(ta.agreed())
                    .build());
        }

        if (!toInsert.isEmpty()) {
            userTermsCommandRepository.saveAll(toInsert);
        }
    }
}
