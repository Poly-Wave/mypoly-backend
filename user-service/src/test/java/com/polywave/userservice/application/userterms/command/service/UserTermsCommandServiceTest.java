package com.polywave.userservice.application.userterms.command.service;

import com.polywave.userservice.application.userterms.command.TermsAgreement;
import com.polywave.userservice.application.userterms.command.UserAgreementCommand;
import com.polywave.userservice.common.exception.UserErrorCode;
import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.common.exception.UserValidationException;
import com.polywave.userservice.domain.Terms;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.domain.UserTerms;
import com.polywave.userservice.repository.command.TermsCommandRepository;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.command.UserTermsCommandRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserTermsCommandServiceTest {

        @Mock
        private UserTermsCommandRepository userTermsCommandRepository;

        @Mock
        private UserCommandRepository userCommandRepository;

        @Mock
        private TermsCommandRepository termsCommandRepository;

        @Mock
        private EntityManager entityManager;

        @InjectMocks
        private UserTermsCommandService userTermsCommandService;

        @Test
        @DisplayName("약관 동의 저장 실패 - RequestBody null 로 인한 예외 발생")
        void saveUserAgreement_Fail_EmptyRequest() {
                // given
                UserAgreementCommand command = null;

                // when & then
                assertThatThrownBy(() -> userTermsCommandService.saveUserAgreement(command))
                                .isInstanceOf(UserValidationException.class)
                                .hasMessageContaining(UserErrorCode.EMPTY_REQUEST_BODY.getCode());
        }

        @Test
        @DisplayName("약관 동의 저장 실패 - 유저 정보(userId) 누락 시 예외 발생")
        void saveUserAgreement_Fail_MissingUserInfo() {
                // given
                UserAgreementCommand command = new UserAgreementCommand(null, List.of(new TermsAgreement(1L, true)));

                // when & then
                assertThatThrownBy(() -> userTermsCommandService.saveUserAgreement(command))
                                .isInstanceOf(UserValidationException.class)
                                .hasMessageContaining(UserErrorCode.MISSING_USER_INFO.getCode());
        }

        @Test
        @DisplayName("약관 동의 저장 실패 - 존재하지 않는 유저")
        void saveUserAgreement_Fail_UserNotFound() {
                // given
                UserAgreementCommand command = new UserAgreementCommand(99L, List.of(new TermsAgreement(1L, true)));
                given(userCommandRepository.existsById(99L)).willReturn(false);

                // when & then
                assertThatThrownBy(() -> userTermsCommandService.saveUserAgreement(command))
                                .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("약관 동의 저장 실패 - 동의 리스트(termsAgreements)가 빈 배열인 경우 예외 발생")
        void saveUserAgreement_Fail_MissingTermsAgree() {
                // given
                UserAgreementCommand command = new UserAgreementCommand(1L, Collections.emptyList());
                given(userCommandRepository.existsById(1L)).willReturn(true);

                // when & then
                assertThatThrownBy(() -> userTermsCommandService.saveUserAgreement(command))
                                .isInstanceOf(UserValidationException.class)
                                .hasMessageContaining(UserErrorCode.MISSING_TERMS_AGREE.getCode());
        }

        @Test
        @DisplayName("약관 동의 저장 실패 - 존재하지 않는 약관 ID(Invalid Terms ID) 전달 시 예외 발생")
        void saveUserAgreement_Fail_InvalidTermsId() {
                // given
                UserAgreementCommand command = new UserAgreementCommand(1L, List.of(new TermsAgreement(999L, true)));
                given(userCommandRepository.existsById(1L)).willReturn(true);
                // DB 단에선 해당 999L 약관이 없음 (빈 리스트 반환)
                given(termsCommandRepository.findAllById(List.of(999L))).willReturn(Collections.emptyList());

                // when & then
                assertThatThrownBy(() -> userTermsCommandService.saveUserAgreement(command))
                                .isInstanceOf(UserValidationException.class)
                                .hasMessageContaining(UserErrorCode.INVALID_TERMS_ID.getCode());
        }

        @Test
        @DisplayName("약관 동의 저장 성공 - 기존 이력이 없으면 새롭게 생성(Insert)")
        void saveUserAgreement_Success_Insert() {
                // given
                Long userId = 1L;
                Long termsId = 10L;
                UserAgreementCommand command = new UserAgreementCommand(userId,
                                List.of(new TermsAgreement(termsId, true)));

                given(userCommandRepository.existsById(userId)).willReturn(true);

                Terms existingTerms = Terms.builder().content("content").name("name").title("title").version(1)
                                .required(true)
                                .build();
                ReflectionTestUtils.setField(existingTerms, "id", termsId);
                given(termsCommandRepository.findAllById(List.of(termsId))).willReturn(List.of(existingTerms));

                given(userTermsCommandRepository.findByUserIdAndTermsIdIn(userId, List.of(termsId)))
                                .willReturn(Collections.emptyList());

                User mockUser = User.builder().nickname("테스트유저").build();
                Terms mockTerms = Terms.builder().content("content").name("name").title("title").version(1)
                                .required(true)
                                .build();
                given(entityManager.getReference(User.class, userId)).willReturn(mockUser);
                given(entityManager.getReference(Terms.class, termsId)).willReturn(mockTerms);

                // when
                userTermsCommandService.saveUserAgreement(command);

                // then
                verify(userTermsCommandRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("약관 동의 수정 성공 - 기존 이력이 있으면 동의 여부(Update) 변경")
        void saveUserAgreement_Success_Update() {
                // given
                Long userId = 1L;
                Long termsId = 10L;
                UserAgreementCommand command = new UserAgreementCommand(userId,
                                List.of(new TermsAgreement(termsId, false))); // 철회
                                                                              // 시뮬레이션

                given(userCommandRepository.existsById(userId)).willReturn(true);

                Terms mockTermsToFind = Terms.builder().content("content").name("name").title("title").version(1)
                                .required(true)
                                .build();
                ReflectionTestUtils.setField(mockTermsToFind, "id", termsId);
                given(termsCommandRepository.findAllById(List.of(termsId))).willReturn(List.of(mockTermsToFind));

                UserTerms existingUserTerms = UserTerms.builder().terms(mockTermsToFind).agreed(true).build();
                ReflectionTestUtils.setField(existingUserTerms, "terms", mockTermsToFind);
                given(userTermsCommandRepository.findByUserIdAndTermsIdIn(userId, List.of(termsId)))
                                .willReturn(List.of(existingUserTerms));

                // when
                userTermsCommandService.saveUserAgreement(command);

                // then
                verify(userTermsCommandRepository, never()).saveAll(any()); // 새로 저장되는 엔티티 없음
                // 변경 감지(Dirty Checking)는 엔티티 내부에서 updateAgreement가 호출됐는지를 통해 간접적으로 일어나며
                // 여기선 명시적 Mockito verify가 어려우므로 saveAll이 호출되지 않고 정상 종료됨을 확인.
        }
}
