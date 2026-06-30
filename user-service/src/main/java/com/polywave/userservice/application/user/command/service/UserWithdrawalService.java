package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.application.user.command.UserWithdrawCommand;
import com.polywave.userservice.client.BillUserDataClient;
import com.polywave.userservice.client.NotificationUserDataClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 탈퇴 오케스트레이션.
 *
 * <p>의도적으로 {@code @Transactional} 을 두지 않는다 — 타 서비스 HTTP 삭제를 DB 트랜잭션 밖에서 수행하여
 * 네트워크 대기(connect 3s / read 5s) 동안 DB 커넥션을 점유하지 않게 한다(커넥션 풀 고갈 방지).
 *
 * <p>순서: (1) 타 서비스 사용자 데이터 삭제(HTTP, 멱등) → (2) user-service soft-delete(짧은 트랜잭션).
 * 부분 실패 시 (2)가 커밋되지 않아 사용자는 ACTIVE 로 남고, 동일 요청 재시도로 복구된다(모든 삭제가 멱등).
 */
@Service
@RequiredArgsConstructor
public class UserWithdrawalService {

    private final BillUserDataClient billUserDataClient;
    private final NotificationUserDataClient notificationUserDataClient;
    private final UserCommandService userCommandService;

    public void withdraw(Long userId, UserWithdrawCommand command) {
        // 1) 타 서비스 사용자 파생 데이터 삭제 (북마크/관심사/조회, 알림). 표결은 보존. (DB 트랜잭션 밖)
        billUserDataClient.deleteUserBillData(userId);
        notificationUserDataClient.deleteUserNotifications(userId);

        // 2) user-service soft-delete (DB 작업만, 짧은 트랜잭션)
        userCommandService.withdraw(userId, command);
    }
}
