package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.application.nickname.policy.NicknameNormalizer;
import com.polywave.userservice.application.nickname.policy.NicknamePolicyService;
import com.polywave.userservice.application.user.command.UserUpdateBasicProfileCommand;
import com.polywave.userservice.application.user.command.UserUpdateProfileCommand;
import com.polywave.userservice.application.user.command.UserWithdrawCommand;
import com.polywave.userservice.common.exception.DuplicateNicknameException;
import com.polywave.userservice.common.exception.ForbiddenNicknameException;
import com.polywave.userservice.common.exception.InvalidOnboardingStatusException;
import com.polywave.userservice.common.exception.UserNotFoundException;
import com.polywave.userservice.domain.OnBoardingStatus;
import com.polywave.userservice.domain.User;
import com.polywave.userservice.repository.command.UserCommandRepository;
import com.polywave.userservice.repository.command.UserTermsCommandRepository;
import com.polywave.userservice.repository.query.UserQueryRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final NicknamePolicyService nicknamePolicyService;
    private final UserTermsCommandRepository userTermsCommandRepository;

    @Override
    public void updateUserProfile(Long userId, UserUpdateProfileCommand command) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.getOnboardingStatus() != OnBoardingStatus.CATEGORY) {
            throw new InvalidOnboardingStatusException();
        }

        String combinedAddress = String.format("%s %s %s",
                command.sido(),
                command.sigungu(),
                command.emdName());

        user.updateProfile(
                command.gender(),
                command.birthdate(),
                command.sido(),
                command.sigungu(),
                command.emdName(),
                combinedAddress);

        updateUserOnboardingStatus(userId, OnBoardingStatus.COMPLETE);
    }

    @Override
    public void updateUserBasicProfile(Long userId, UserUpdateBasicProfileCommand command) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        validateBasicProfileUpdatable(user);
        updateNicknameIfNeeded(user, command.nickname());

        String combinedAddress = String.format("%s %s %s",
                command.sido(),
                command.sigungu(),
                command.emdName());

        user.updateProfile(
                command.gender(),
                command.birthDate(),
                command.sido(),
                command.sigungu(),
                command.emdName(),
                combinedAddress);
    }

    @Override
    public void updateUserOnboardingStatus(Long userId, OnBoardingStatus onBoardingStatus) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 마일스톤 시각은 immutable 로 기록한다 (markXxxIfAbsent).
        // 이미 채워져 있으면 덮어쓰지 않아 D+1 리마인더 알림이 중복 발송되지 않는다.
        //
        // 매핑 근거 (회원가입 흐름):
        //   회원가입 직후 SocialTokenAuthService.signup() 가 닉네임을 박은 채 SIGNUP 으로 PATCH 한다.
        //   따라서 "별명 설정 완료" = SIGNUP 진입 시점이다.
        //   ONBOARDING 은 클라이언트가 임의로 PATCH 하는 중간 단계라 별명/카테고리/추가정보 중 어디와도
        //   직접 매핑되지 않으므로 마일스톤을 박지 않는다.
        Instant now = Instant.now();
        if (onBoardingStatus == OnBoardingStatus.SIGNUP) {
            user.markNicknameSetAtIfAbsent(now);
        } else if (onBoardingStatus == OnBoardingStatus.CATEGORY) {
            user.markCategorySetAtIfAbsent(now);
        } else if (onBoardingStatus == OnBoardingStatus.COMPLETE) {
            user.markProfileCompletedAtIfAbsent(now);
        }

        user.updateOnBoardingStatus(onBoardingStatus);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userCommandRepository.delete(user);
    }

    /**
     * 회원 탈퇴의 user-service DB 작업(soft-delete)만 담당한다.
     * 타 서비스 데이터 삭제(북마크/관심사/조회/알림)는 트랜잭션 밖에서 {@code UserWithdrawalService} 가 선행 호출한다.
     */
    @Override
    public void withdraw(Long userId, UserWithdrawCommand command) {
        User user = userCommandRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.isWithdrawn()) {
            // 이미 탈퇴한 계정 — 멱등하게 처리(없는 사용자 취급).
            throw new UserNotFoundException();
        }

        // 약관 동의 내역 말소(재가입 시 재동의로 채워짐).
        userTermsCommandRepository.deleteAllByUserId(userId);

        // soft-delete: PII 말소 + WITHDRAWN + 세션 무효화 + 사유 기록.
        // user 행/uid 와 user_oauths 매핑은 보존되어 7일 후 재가입 시 같은 uid 로 재활성된다.
        user.withdraw(command.reasons(), command.etcText());
    }

    private void validateBasicProfileUpdatable(User user) {
        if (user.getOnboardingStatus() != OnBoardingStatus.COMPLETE) {
            throw new InvalidOnboardingStatusException();
        }
    }

    private void updateNicknameIfNeeded(User user, String rawNickname) {
        String normalizedNickname = NicknameNormalizer.normalize(rawNickname);
        String currentNickname = NicknameNormalizer.normalize(user.getNickname());

        if (normalizedNickname.equals(currentNickname)) {
            return;
        }

        if (nicknamePolicyService.isForbidden(normalizedNickname)) {
            throw new ForbiddenNicknameException();
        }

        if (userQueryRepository.existsByNickname(normalizedNickname)) {
            throw new DuplicateNicknameException();
        }

        user.changeNickname(normalizedNickname);
    }
}