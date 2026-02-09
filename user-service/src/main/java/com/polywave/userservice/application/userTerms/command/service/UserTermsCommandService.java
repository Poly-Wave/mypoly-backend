package com.polywave.userservice.application.userTerms.command.service;

import com.polywave.userservice.application.userTerms.command.TermsAgreement;
import com.polywave.userservice.application.userTerms.command.UserAgreementCommand;
import com.polywave.userservice.domain.Terms;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserTerms;
import com.polywave.userservice.repository.command.TermsCommandRepository;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.command.UserTermsCommandRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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
            throw new IllegalArgumentException("요청 본문이 비어있습니다.");
        }

        Long userId = command.userId();
        List<TermsAgreement> termAgreements = command.termsAgreements();

        if (userId == null) {
            // 정상적이면 @LoginUser로 들어오니 거의 발생 안 하지만, NPE 방지용
            throw new IllegalArgumentException("인증 사용자 정보가 없습니다.");
        }

        if (!userCommandRepository.existsById(userId)) {
            throw new EntityNotFoundException("유저를 찾을 수 없습니다. userId=" + userId);
        }

        if (termAgreements == null || termAgreements.isEmpty()) {
            throw new IllegalArgumentException("약관 동의 정보가 비어있습니다.");
        }

        // termId 중복 제거 + null 체크
        List<Long> termIds = termAgreements.stream()
                .filter(Objects::nonNull)
                .map(TermsAgreement::termId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (termIds.isEmpty()) {
            throw new IllegalArgumentException("약관 ID(termId)가 비어있습니다.");
        }

        // ✅ termsId 존재 검증 (없는 ID가 섞여있으면 깔끔하게 400으로 반환)
        Set<Long> existingTermIds = termsCommandRepository.findAllById(termIds).stream()
                .map(Terms::getId)
                .collect(Collectors.toSet());

        List<Long> missing = termIds.stream()
                .filter(id -> !existingTermIds.contains(id))
                .toList();

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 약관 ID가 포함되어 있습니다: " + missing);
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
            if (ta == null || ta.termId() == null) {
                // DTO에서 @NotNull로 막히는 게 정상이라 여기까지 오면 요청이 이상한 케이스
                throw new IllegalArgumentException("약관 ID(termId)는 필수입니다.");
            }

            UserTerms current = existingMap.get(ta.termId());
            if (current != null) {
                current.updateAgreement(ta.agreed(), now);
                continue;
            }

            // 위에서 존재 검증을 끝냈기 때문에 FK/EntityNotFound로 터질 가능성을 제거함
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
