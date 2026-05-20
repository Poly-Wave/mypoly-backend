package com.polywave.notificationservice.domain.notification;

/**
 * 알림 클릭 시 이동할 화면 타입.
 *
 * 백엔드는 어디로 이동해야 하는지 판단할 수 있는 랜딩 정보를 내려주고,
 * 실제 라우팅은 앱에서 landingType 기준으로 처리한다.
 *
 * - NOTICE_DETAIL     : 공지 상세 (landingId = 공지 ID)
 * - BILL_DETAIL       : 의안 상세 (landingId = 의안 ID)
 * - SUBSIDY_DETAIL    : 보조금 상세 (landingId = 보조금 ID)
 * - NOTIFICATION_LIST : 앱 알림함 화면 자체로 이동 (푸시에서 사용)
 * - EXTERNAL_URL      : 외부 URL (landingUrl 만 사용)
 * - NONE              : 랜딩 대상 없음
 */
public enum LandingType {
    NOTICE_DETAIL,
    BILL_DETAIL,
    SUBSIDY_DETAIL,
    NOTIFICATION_LIST,
    EXTERNAL_URL,
    NONE
}
