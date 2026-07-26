package com.polywave.notificationservice.application.notification.policy;

import java.util.Map;
import java.util.Set;

/**
 * 시스템 알림 정책 식별 키.
 *
 * - DB 의 notification_policies.policy_key 와 1:1 매칭된다.
 * - 스케줄러/이벤트 핸들러가 정책을 찾을 때 ID 가 아닌 이 키로 lookup 한다.
 *   (정책이 DB 에서 재생성되어 ID 가 바뀌어도 운영 코드는 영향 없음)
 * - 시드 시점에는 status = READY 로 시작하고, 운영자가 ACTIVE 로 전환해야 실제 발송된다.
 *
 * 각 키마다 발급 시점에 어떤 템플릿 토큰을 supply 하는지 함께 선언한다.
 * NotificationPolicyCommandService 가 PATCH 시점에 title/body 안의 {...} 토큰이
 * 이 화이트리스트에 들어 있는지 검증하여 오타로 인한 미치환 발송을 사전에 차단한다.
 *
 * 토큰을 추가/제거할 때는 반드시 해당 스케줄러의 templateVars 와 함께 동기화해야 한다.
 */
public final class SystemNotificationPolicyKey {

    /** 행 1: 별명 설정 완료 + 관심 주제 미선택 유저 → 별명 설정 D+1일 리마인더 */
    public static final String ONBOARDING_NICKNAME_REMIND_D1 = "ONBOARDING_NICKNAME_REMIND_D1";

    /** 행 2: 관심 주제 선택 완료 + 추가 정보 미입력 유저 → 관심 주제 선택 D+1일 리마인더 */
    public static final String ONBOARDING_CATEGORY_REMIND_D1 = "ONBOARDING_CATEGORY_REMIND_D1";

    /** 행 4: 관심 주제 선택 완료 유저 → 매일 18:00 "요즘 핫한 안건" 모아보기 */
    public static final String HOME_TRENDING_AGENDA_DAILY = "HOME_TRENDING_AGENDA_DAILY";

    /** 행 5: 관심 주제 선택 완료 유저 → 매일 18:00 "최근 30일 인기 안건" 모아보기 */
    public static final String HOME_RECENT_30D_POPULAR_DAILY = "HOME_RECENT_30D_POPULAR_DAILY";

    /** 행 7: 북마크 저장 후 D+1일 해당 의안에 투표 안 한 유저 → 의안별 1회 리마인더 */
    public static final String BILL_BOOKMARK_NO_VOTE_REMIND_D1 = "BILL_BOOKMARK_NO_VOTE_REMIND_D1";

    /** 행 3: 관심 카테고리에 매칭되는 신규 안건이 있을 때 → 매일 09:00 KST 알림 */
    public static final String HOME_NEW_INTEREST_AGENDA_DAILY = "HOME_NEW_INTEREST_AGENDA_DAILY";

    /** 행 6: 북마크된 의안의 진행 단계가 바뀌었을 때 (10분 polling) */
    public static final String BILL_STAGE_CHANGE_NOTIFY = "BILL_STAGE_CHANGE_NOTIFY";

    /** 공지사항이 새로 등록(노출)됐을 때 → 전체 유저 broadcast (5분 polling) */
    public static final String NOTICE_PUBLISHED_BROADCAST = "NOTICE_PUBLISHED_BROADCAST";

    /**
     * 정책 키별로 발급 시점에 supply 되는 템플릿 토큰 집합.
     * - 운영자가 PATCH 로 본문을 수정해 이 집합 밖의 토큰을 끼워넣으면 검증에서 거부된다.
     * - 토큰을 늘리려면 해당 스케줄러의 templateVars 도 같이 수정해야 한다.
     */
    private static final Map<String, Set<String>> SUPPORTED_TOKENS = Map.of(
            ONBOARDING_NICKNAME_REMIND_D1,    Set.of("별명"),
            ONBOARDING_CATEGORY_REMIND_D1,    Set.of("별명"),
            HOME_TRENDING_AGENDA_DAILY,       Set.of("별명"),
            HOME_RECENT_30D_POPULAR_DAILY,    Set.of("별명"),
            HOME_NEW_INTEREST_AGENDA_DAILY,   Set.of("별명", "개수"),
            BILL_BOOKMARK_NO_VOTE_REMIND_D1,  Set.of("안건 제목"),
            BILL_STAGE_CHANGE_NOTIFY,         Set.of("안건 제목", "이전 단계", "현재 단계"),
            NOTICE_PUBLISHED_BROADCAST,       Set.of("제목")
    );

    /**
     * 시스템 정책의 지원 토큰 집합. 시스템 정책이 아니면 null 반환 (= 검증 skip).
     */
    public static Set<String> supportedTokens(String policyKey) {
        if (policyKey == null) {
            return null;
        }
        return SUPPORTED_TOKENS.get(policyKey);
    }

    private SystemNotificationPolicyKey() {
    }
}
