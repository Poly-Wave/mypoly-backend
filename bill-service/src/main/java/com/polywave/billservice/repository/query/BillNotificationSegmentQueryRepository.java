package com.polywave.billservice.repository.query;

import com.polywave.billservice.application.notification.query.result.BillStageChangeResult;
import com.polywave.billservice.application.notification.query.result.BookmarkedUnvotedBillResult;
import com.polywave.billservice.application.notification.query.result.UserInterestAgendaCountResult;
import java.time.Instant;
import java.util.List;

/**
 * 알림 발급 대상 산출용 segment query.
 *
 * - 노션 알림 리스트 중 bill-service 데이터에 의존하는 행들의 발급 대상을 산출한다.
 * - notification-service 가 internal API 로 호출한다.
 */
public interface BillNotificationSegmentQueryRepository {

    /**
     * 행 7: 북마크 저장 시각이 cutoff 이전이고, 같은 (userId, billId) 로 투표하지 않은 (user, bill) 쌍.
     */
    List<BookmarkedUnvotedBillResult> findBookmarkedUnvotedBefore(Instant cutoff);

    /**
     * 행 3: 사용자의 관심 카테고리와 매칭되는, [start, end) 범위에 first_collected_at 이 들어가는 안건 카운트.
     * count > 0 인 사용자만 결과에 포함된다.
     */
    List<UserInterestAgendaCountResult> findInterestMatchedAgendaCountsBetween(Instant start, Instant end);

    /**
     * 행 6: since 이후 단계가 변경된 의안 × 그 의안을 (변경 시점 이전에) 북마크한 사용자.
     * 한 단계 전이당 한 row. fromStage 가 null 이면 최초 단계 진입.
     */
    List<BillStageChangeResult> findRecentBookmarkedStageChanges(Instant since);
}
