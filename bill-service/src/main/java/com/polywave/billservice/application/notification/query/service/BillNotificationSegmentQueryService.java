package com.polywave.billservice.application.notification.query.service;

import com.polywave.billservice.application.notification.query.result.BillStageChangeResult;
import com.polywave.billservice.application.notification.query.result.BookmarkedUnvotedBillResult;
import com.polywave.billservice.application.notification.query.result.UserInterestAgendaCountResult;
import com.polywave.billservice.repository.query.BillNotificationSegmentQueryRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * notification-service 가 호출하는 의안 세그먼트 조회 application service.
 *
 * - InternalBillSegmentController 가 repository 를 직접 호출하지 않고 이 service 를 경유한다.
 *   (다른 controller 들과 동일한 controller → service → repository 레이어 컨벤션)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillNotificationSegmentQueryService {

    private final BillNotificationSegmentQueryRepository billNotificationSegmentQueryRepository;

    public List<BookmarkedUnvotedBillResult> findBookmarkedUnvotedBefore(Instant before) {
        return billNotificationSegmentQueryRepository.findBookmarkedUnvotedBefore(before);
    }

    public List<UserInterestAgendaCountResult> findInterestMatchedAgendaCountsBetween(Instant from, Instant to) {
        return billNotificationSegmentQueryRepository.findInterestMatchedAgendaCountsBetween(from, to);
    }

    public List<BillStageChangeResult> findRecentBookmarkedStageChanges(Instant since) {
        return billNotificationSegmentQueryRepository.findRecentBookmarkedStageChanges(since);
    }
}
