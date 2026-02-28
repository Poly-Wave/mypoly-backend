package com.polywave.userservice.application.userterms.command.service;

import com.polywave.userservice.application.userterms.command.TermsAgreement;
import com.polywave.userservice.application.userterms.command.UserAgreementCommand;
import com.polywave.userservice.domain.Terms;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserTerms;
import com.polywave.userservice.repository.command.TermsCommandRepository;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.command.UserTermsCommandRepository;
import jakarta.persistence.EntityManager;
import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.common.exception.UserValidationException;
import com.polywave.userservice.common.exception.UserErrorCode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTermsCommandService {

    private final UserTermsCommandRepository userTermsCommandRepository;
    private final UserCommandRepository userCommandRepository;
    private final TermsCommandRepository termsCommandRepository;
    private final EntityManager entityManager;

    /**
     * 업서트 방식:
     * - (user_id, terms_id) 유니크 보장
     * - 있으면 update(agreed/agreedAt), 없으면 insert
     *
     * 예외 정책:
     * - userId가 없거나 요청이 비정상이면: IllegalArgumentException -> 400
     * - userId가 존재하지 않으면: EntityNotFoundException -> 404
     * - termId가 DB에 존재하지 않으면: IllegalArgumentException -> 400 (클라이언트 요청 오류)
     */
    @Transactional
    public void saveUserAgreement(UserAgreementCommand command) {
        if (command == null) {
            throw new UserValidationException(UserErrorCode.EMPTY_REQUEST_BODY);
        }

        Long userId = command.userId();
        List<TermsAgreement> termAgreements = command.termsAgreements();

        if (userId == null) {
            throw new UserValidationException(UserErrorCode.MISSING_USER_INFO);
        }

        if (!userCommandRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        if (termAgreements == null || termAgreements.isEmpty()) {
            throw new UserValidationException(UserErrorCode.MISSING_TERMS_AGREE);
        }

        List<Long> termIds = termAgreements.stream()
                .filter(Objects::nonNull)
                .map(TermsAgreement::termId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (termIds.isEmpty()) {
            throw new UserValidationException(UserErrorCode.MISSING_TERMS_ID);
        }

        Set<Long> existingTermIds = termsCommandRepository.findAllById(termIds).stream()
                .map(Terms::getId)
                .collect(Collectors.toSet());

        List<Long> missing = termIds.stream()
                .filter(id -> !existingTermIds.contains(id))
                .toList();

        if (!missing.isEmpty()) {
            throw new UserValidationException(UserErrorCode.INVALID_TERMS_ID);
        }

        List<UserTerms> existing = userTermsCommandRepository.findByUserIdAndTermsIdIn(userId, termIds);
        Map<Long, UserTerms> existingMap = existing.stream()
                .collect(Collectors.toMap(
                        ut -> ut.getTerms().getId(),
                        Function.identity(),
                        (a, b) -> a));

        User userProxy = entityManager.getReference(User.class, userId);
        Instant now = Instant.now();

        List<UserTerms> toInsert = new ArrayList<>();

        for (TermsAgreement ta : termAgreements) {
            if (ta == null || ta.termId() == null) {
                throw new UserValidationException(UserErrorCode.MISSING_TERMS_ID);
            }

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
