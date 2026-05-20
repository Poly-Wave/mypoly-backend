package com.polywave.userservice.api.controller.internal;

import com.polywave.userservice.api.dto.OnboardingReminderUserResponse;
import com.polywave.userservice.repository.query.UserSegmentQueryRepository;
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
 * 다른 서비스(notification-service 등) 가 호출하는 사용자 세그먼트 internal API.
 *
 * - InternalApiKeyFilter 가 X-Internal-Api-Key 헤더로 가드한다.
 * - 사용자 JWT 가 없는 컨텍스트(스케줄러 등) 에서 호출된다.
 */
@Tag(name = "Internal User Segment", description = "[Internal] 알림 발급 대상 사용자 세그먼트 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/segments")
public class InternalUserSegmentController {

    private final UserSegmentQueryRepository userSegmentQueryRepository;

    @Operation(summary = "[Internal] 온보딩 리마인더 발급 대상 조회", description = """
            type = NICKNAME : 별명 설정 완료 + 관심 주제 미선택 + nickname_set_at < before
            type = CATEGORY : 관심 주제 선택 완료 + 추가 정보 미입력 + category_set_at < before
            """)
    @GetMapping("/onboarding-reminder")
    public ResponseEntity<List<OnboardingReminderUserResponse>> getOnboardingReminderTargets(
            @Parameter(in = ParameterIn.HEADER, name = "X-Internal-Api-Key", description = "서비스 간 internal 공유 키", required = true)
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String internalApiKey,

            @Parameter(description = "세그먼트 타입", example = "NICKNAME")
            @RequestParam Type type,

            @Parameter(description = "cutoff 시각(이 시각 이전에 마일스톤 도달한 유저)", example = "2026-05-19T03:00:00Z")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before
    ) {
        List<OnboardingReminderUserResponse> response = switch (type) {
            case NICKNAME -> userSegmentQueryRepository.findUsersStuckAtNicknameBefore(before).stream()
                    .map(OnboardingReminderUserResponse::from)
                    .toList();
            case CATEGORY -> userSegmentQueryRepository.findUsersStuckAtCategoryBefore(before).stream()
                    .map(OnboardingReminderUserResponse::from)
                    .toList();
        };
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[Internal] 온보딩 완료 유저 전원 조회", description = """
            profile_completed_at != null 인 유저 전원을 반환한다.
            행 3·4·5 의 fan-out 알림 대상이다.
            """)
    @GetMapping("/onboarding-completed")
    public ResponseEntity<List<OnboardingReminderUserResponse>> getOnboardingCompletedTargets(
            @Parameter(in = ParameterIn.HEADER, name = "X-Internal-Api-Key", description = "서비스 간 internal 공유 키", required = true)
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String internalApiKey
    ) {
        List<OnboardingReminderUserResponse> response = userSegmentQueryRepository
                .findOnboardingCompletedUsers().stream()
                .map(OnboardingReminderUserResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    public enum Type {
        NICKNAME,
        CATEGORY
    }
}
