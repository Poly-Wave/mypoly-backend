package com.polywave.notificationservice.application.notification.policy;

import com.polywave.notificationservice.domain.notification.LandingType;
import com.polywave.notificationservice.domain.notification.NotificationCategory;
import com.polywave.notificationservice.domain.notification.NotificationChannel;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import com.polywave.notificationservice.repository.command.NotificationPolicyCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 시스템이 자동 발급하는 알림 정책을 부팅 시점에 멱등하게 시드한다.
 *
 * - policy_key 가 UNIQUE 이므로 이미 존재하면 skip.
 * - 시드된 정책은 status = READY 로 시작하며, 운영자가 PATCH /internal/notification-policies/{id}/status 로
 *   ACTIVE 전환해야 실제 발송이 시작된다 (운영 사고 방지).
 * - 운영자가 본문/타이틀을 PATCH 로 수정해도 policy_key 는 그대로 유지되므로 스케줄러 lookup 은 영향받지 않는다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SystemNotificationPolicySeeder implements ApplicationRunner {

    private final NotificationPolicyCommandRepository repository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedIfAbsent(NotificationPolicy.builder()
                .policyKey(SystemNotificationPolicyKey.ONBOARDING_NICKNAME_REMIND_D1)
                .name("별명 설정 완료 + 관심 주제 미선택 유저 리마인더")
                .depth("온보딩")
                .channel(NotificationChannel.PUSH)
                .category(NotificationCategory.ETC)
                .targetAudience("별명 설정 완료 + 관심 주제 미선택 유저")
                .sendSchedule("별명 설정 D+1일 12:00 KST")
                .title("AI가 관심있는 안건만 모아드려요.")
                .body("{별명}님, 관심 주제를 선택하면 필요한 안건만 모아볼 수 있어요.")
                .landingType(LandingType.NOTIFICATION_LIST)
                .landingUrl(null)
                .status(NotificationPolicyStatus.READY)
                .build());

        seedIfAbsent(NotificationPolicy.builder()
                .policyKey(SystemNotificationPolicyKey.ONBOARDING_CATEGORY_REMIND_D1)
                .name("관심 주제 선택 완료 + 추가 정보 미입력 유저 리마인더")
                .depth("온보딩")
                .channel(NotificationChannel.PUSH)
                .category(NotificationCategory.ETC)
                .targetAudience("관심 주제 선택 완료 + 추가 정보 미입력 유저")
                .sendSchedule("관심 주제 선택 D+1일 12:00 KST")
                .title("{별명}님께 더 잘 맞는 안건을 추천해드릴게요.")
                .body("추가 정보를 입력하시면 거주 지역, 세대에 맞는 안건도 함께 모아볼 수 있어요.")
                .landingType(LandingType.NOTIFICATION_LIST)
                .landingUrl(null)
                .status(NotificationPolicyStatus.READY)
                .build());

        seedIfAbsent(NotificationPolicy.builder()
                .policyKey(SystemNotificationPolicyKey.HOME_TRENDING_AGENDA_DAILY)
                .name("홈_요즘 핫한 안건 데일리 broadcast")
                .depth("홈")
                .channel(NotificationChannel.PUSH)
                .category(NotificationCategory.BILL)
                .targetAudience("관심 주제 선택 완료 유저")
                .sendSchedule("매일 18:00 KST")
                .title("요즘 핫한 안건들 모아보기")
                .body("{별명}님, 요즘 핫한 안건들을 모아봤어요. 다른 사람들은 어떤 의견을 갖고 있는지 확인해 보세요.")
                .landingType(LandingType.NOTIFICATION_LIST)
                .landingUrl(null)
                .status(NotificationPolicyStatus.READY)
                .build());

        seedIfAbsent(NotificationPolicy.builder()
                .policyKey(SystemNotificationPolicyKey.HOME_NEW_INTEREST_AGENDA_DAILY)
                .name("홈_관심 카테고리 신규 안건 데일리")
                .depth("홈")
                .channel(NotificationChannel.PUSH)
                .category(NotificationCategory.BILL)
                .targetAudience("관심 주제 선택 완료 유저 (관심 카테고리 매칭 신규 안건 존재)")
                .sendSchedule("매일 09:00 KST")
                .title("새로 올라온 안건이 {개수}개 있어요.")
                .body("{별명}님이 관심있어 하는 주제에 새로 올라온 안건이 {개수}개 있어요. 어떤 내용인지 확인해 보세요.")
                .landingType(LandingType.NOTIFICATION_LIST)
                .landingUrl(null)
                .status(NotificationPolicyStatus.READY)
                .build());

        seedIfAbsent(NotificationPolicy.builder()
                .policyKey(SystemNotificationPolicyKey.BILL_STAGE_CHANGE_NOTIFY)
                .name("북마크 의안 단계 변경 알림")
                .depth("안건")
                .channel(NotificationChannel.PUSH)
                .category(NotificationCategory.BILL)
                .targetAudience("관심 주제 선택 완료 + 북마크 안건 있는 유저")
                .sendSchedule("배치 직후 (10분 polling)")
                .title("{안건 제목} 안건의 진행 단계가 바뀌었어요.")
                .body("안건 : {안건 제목}\n진행 상태 : {이전 단계} → {현재 단계}")
                .landingType(LandingType.BILL_DETAIL)
                .landingUrl(null)
                .status(NotificationPolicyStatus.READY)
                .build());

        seedIfAbsent(NotificationPolicy.builder()
                .policyKey(SystemNotificationPolicyKey.BILL_BOOKMARK_NO_VOTE_REMIND_D1)
                .name("북마크 D+1 미투표 리마인더")
                .depth("안건")
                .channel(NotificationChannel.PUSH)
                .category(NotificationCategory.BILL)
                .targetAudience("북마크 저장 + D+1 미투표 유저")
                .sendSchedule("매일 12:00 KST")
                .title("관심있는 안건에 투표하고 결과 확인하기")
                .body("{안건 제목} 안건에 관심이 있으신가요? 사람들에게 투표로 안건에 대한 의견을 알리고, 결과를 확인해 보세요.")
                .landingType(LandingType.BILL_DETAIL)
                .landingUrl(null)
                .status(NotificationPolicyStatus.READY)
                .build());

        seedIfAbsent(NotificationPolicy.builder()
                .policyKey(SystemNotificationPolicyKey.HOME_RECENT_30D_POPULAR_DAILY)
                .name("홈_최근 30일 인기 안건 데일리 broadcast")
                .depth("홈")
                .channel(NotificationChannel.PUSH)
                .category(NotificationCategory.BILL)
                .targetAudience("관심 주제 선택 완료 유저")
                .sendSchedule("매일 18:00 KST")
                .title("최근 30일 핫했던 안건 모아보기")
                .body("{별명}님, 최근 30일 동안 핫했던 안건들을 모아봤어요. 다른 사람들은 어떤 의견을 갖고 있는지 확인해 보세요.")
                .landingType(LandingType.NOTIFICATION_LIST)
                .landingUrl(null)
                .status(NotificationPolicyStatus.READY)
                .build());
    }

    private void seedIfAbsent(NotificationPolicy policy) {
        if (repository.findByPolicyKey(policy.getPolicyKey()).isPresent()) {
            return;
        }
        repository.save(policy);
        log.info("Seeded system notification policy: policyKey={}", policy.getPolicyKey());
    }
}
