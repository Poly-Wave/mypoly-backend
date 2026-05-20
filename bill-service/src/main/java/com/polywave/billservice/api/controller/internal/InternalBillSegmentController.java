package com.polywave.billservice.api.controller.internal;

import com.polywave.billservice.api.dto.BillStageChangeResponse;
import com.polywave.billservice.api.dto.BookmarkedUnvotedBillResponse;
import com.polywave.billservice.api.dto.UserInterestAgendaCountResponse;
import com.polywave.billservice.repository.query.BillNotificationSegmentQueryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * notification-service 가 호출하는 의안 세그먼트 internal API.
 *
 * - InternalApiKeyFilter 가 X-Internal-Api-Key 헤더로 가드한다.
 * - 사용자 JWT 가 없는 컨텍스트(스케줄러 등) 에서 호출된다.
 */
@Tag(name = "Internal Bill Segment", description = "[Internal] 알림 발급 대상 의안 세그먼트 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/segments")
public class InternalBillSegmentController {

    private final BillNotificationSegmentQueryRepository billNotificationSegmentQueryRepository;

    @Operation(summary = "[Internal] 북마크 D+1 미투표 (user, bill) 쌍 조회", description = """
            북마크 저장 시각이 before 이전이고, 같은 사용자가 그 의안에 아직 투표하지 않은 (user, bill) 쌍을 반환한다.
            행 7 (북마크 저장 후 D+1일 미투표) 알림의 발급 대상.
            """)
    @GetMapping("/bookmarked-unvoted")
    public ResponseEntity<List<BookmarkedUnvotedBillResponse>> getBookmarkedUnvotedTargets(
            @Parameter(in = ParameterIn.HEADER, name = "X-Internal-Api-Key", description = "서비스 간 internal 공유 키", required = true)
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String internalApiKey,

            @Parameter(description = "북마크 저장 시각 cutoff(이 시각 이전에 북마크한 (user, bill) 쌍)", example = "2026-05-19T03:00:00Z")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before
    ) {
        List<BookmarkedUnvotedBillResponse> response = billNotificationSegmentQueryRepository
                .findBookmarkedUnvotedBefore(before).stream()
                .map(BookmarkedUnvotedBillResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[Internal] 관심 카테고리 매칭 신규 안건 카운트", description = """
            [from, to) 범위에 first_collected_at 이 들어간 안건 중 사용자의 관심 카테고리에 매칭되는 것의 개수를 사용자별로 집계.
            count > 0 인 사용자만 결과에 포함된다.
            행 3 (매일 09:00 관심 카테고리 매칭) 알림의 발급 대상.
            """)
    @GetMapping("/interest-matched-counts")
    public ResponseEntity<List<UserInterestAgendaCountResponse>> getInterestMatchedCounts(
            @Parameter(in = ParameterIn.HEADER, name = "X-Internal-Api-Key", description = "서비스 간 internal 공유 키", required = true)
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String internalApiKey,

            @Parameter(description = "수집 시작 시각 (inclusive)", example = "2026-05-20T00:00:00Z")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,

            @Parameter(description = "수집 종료 시각 (exclusive)", example = "2026-05-21T00:00:00Z")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        List<UserInterestAgendaCountResponse> response = billNotificationSegmentQueryRepository
                .findInterestMatchedAgendaCountsBetween(from, to).stream()
                .map(UserInterestAgendaCountResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[Internal] 북마크된 의안의 최근 단계 전이", description = """
            since 이후 단계가 변경된 의안 × 그 시점 이전에 북마크한 사용자.
            행 6 (북마크 단계 변경 실시간) 알림 발급 대상.
            """)
    @GetMapping("/bookmarked-stage-changes")
    public ResponseEntity<List<BillStageChangeResponse>> getBookmarkedStageChanges(
            @Parameter(in = ParameterIn.HEADER, name = "X-Internal-Api-Key", description = "서비스 간 internal 공유 키", required = true)
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String internalApiKey,

            @Parameter(description = "이 시각 이후 발생한 전이만 반환", example = "2026-05-20T23:00:00Z")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since
    ) {
        List<BillStageChangeResponse> response = billNotificationSegmentQueryRepository
                .findRecentBookmarkedStageChanges(since).stream()
                .map(BillStageChangeResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
