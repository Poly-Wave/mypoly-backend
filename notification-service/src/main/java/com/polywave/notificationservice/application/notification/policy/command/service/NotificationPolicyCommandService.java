package com.polywave.notificationservice.application.notification.policy.command.service;

import com.polywave.notificationservice.application.notification.policy.SystemNotificationPolicyKey;
import com.polywave.notificationservice.application.notification.policy.command.NotificationPolicyCommand;
import com.polywave.notificationservice.common.exception.InvalidNotificationPolicyException;
import com.polywave.notificationservice.common.exception.NotificationPolicyNotFoundException;
import com.polywave.notificationservice.domain.notification.LandingType;
import com.polywave.notificationservice.domain.notification.NotificationPolicy;
import com.polywave.notificationservice.domain.notification.NotificationPolicyStatus;
import com.polywave.notificationservice.repository.command.NotificationPolicyCommandRepository;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPolicyCommandService {

    /** 본문/타이틀에서 {토큰} 형태를 추출하기 위한 정규식. 중괄호 안에 공백/한글 허용, 빈 토큰은 제외. */
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{([^{}]+?)\\}");

    private final NotificationPolicyCommandRepository notificationPolicyCommandRepository;

    public NotificationPolicy create(NotificationPolicyCommand command) {
        validate(command);
        validateTokensForSystemPolicy(command.policyKey(), command.title(), command.body());

        NotificationPolicy policy = NotificationPolicy.builder()
                .policyKey(blankToNull(command.policyKey()))
                .name(command.name().trim())
                .depth(blankToNull(command.depth()))
                .channel(command.channel())
                .category(command.category())
                .targetAudience(blankToNull(command.targetAudience()))
                .sendSchedule(blankToNull(command.sendSchedule()))
                .title(blankToNull(command.title()))
                .body(command.body().trim())
                .landingType(command.landingType())
                .landingUrl(blankToNull(command.landingUrl()))
                .status(NotificationPolicyStatus.READY)
                .build();

        return notificationPolicyCommandRepository.save(policy);
    }

    public NotificationPolicy update(Long policyId, NotificationPolicyCommand command) {
        validate(command);

        NotificationPolicy policy = notificationPolicyCommandRepository.findById(policyId)
                .orElseThrow(NotificationPolicyNotFoundException::new);

        validateTokensForSystemPolicy(policy.getPolicyKey(), command.title(), command.body());

        policy.update(
                command.name().trim(),
                blankToNull(command.depth()),
                command.channel(),
                command.category(),
                blankToNull(command.targetAudience()),
                blankToNull(command.sendSchedule()),
                blankToNull(command.title()),
                command.body().trim(),
                command.landingType(),
                blankToNull(command.landingUrl())
        );
        return policy;
    }

    public NotificationPolicy changeStatus(Long policyId, NotificationPolicyStatus status) {
        if (status == null) {
            throw new InvalidNotificationPolicyException();
        }

        NotificationPolicy policy = notificationPolicyCommandRepository.findById(policyId)
                .orElseThrow(NotificationPolicyNotFoundException::new);

        policy.changeStatus(status);
        return policy;
    }

    private void validate(NotificationPolicyCommand command) {
        if (command == null
                || command.name() == null || command.name().isBlank()
                || command.body() == null || command.body().isBlank()
                || command.channel() == null
                || command.category() == null
                || command.landingType() == null) {
            throw new InvalidNotificationPolicyException();
        }

        if (command.landingType() == LandingType.EXTERNAL_URL
                && (command.landingUrl() == null || command.landingUrl().isBlank())) {
            throw new InvalidNotificationPolicyException();
        }
    }

    /**
     * policy_key 가 시스템 정책일 경우, title/body 안의 {토큰} 이 화이트리스트에 들어 있는지 검증한다.
     * - 시스템 정책이 아니거나 unknown 키면 검증 skip (자유 본문 허용).
     * - 위반 시 InvalidNotificationPolicyException (400) 으로 PATCH 자체를 거부 → 사고 차단.
     */
    private void validateTokensForSystemPolicy(String policyKey, String title, String body) {
        Set<String> supported = SystemNotificationPolicyKey.supportedTokens(policyKey);
        if (supported == null) {
            return;
        }
        Set<String> used = new LinkedHashSet<>();
        extractTokens(title, used);
        extractTokens(body, used);
        for (String token : used) {
            if (!supported.contains(token)) {
                throw new InvalidNotificationPolicyException();
            }
        }
    }

    private void extractTokens(String text, Set<String> out) {
        if (text == null || text.isEmpty()) {
            return;
        }
        Matcher m = TOKEN_PATTERN.matcher(text);
        while (m.find()) {
            String token = m.group(1).trim();
            if (!token.isEmpty()) {
                out.add(token);
            }
        }
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
