package com.polywave.notificationservice.application.notification.template;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 알림 정책 본문/타이틀에 포함된 변수 자리를 호출 시점의 값으로 치환한다.
 *
 * - 지원 문법: {key} 또는 {nickname} 같은 ASCII 키와 {별명} 같은 한글 키 모두 허용.
 * - 입력에 변수가 없거나 매핑이 비어 있으면 원본을 그대로 반환한다.
 * - 치환 결과에 {...} 가 남으면 WARN 로그를 남긴다 (시더 오타/스케줄러 누락 추적).
 *   사용자에게 발송되는 본문 자체는 fail-soft 로 placeholder 가 그대로 남는다.
 */
@Component
@Slf4j
public class MessageTemplateRenderer {

    private static final Pattern UNRESOLVED_TOKEN_PATTERN = Pattern.compile("\\{([^{}]+?)\\}");

    public String render(String template, Map<String, String> templateVars) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        String result = template;
        if (templateVars != null && !templateVars.isEmpty()) {
            for (Map.Entry<String, String> e : templateVars.entrySet()) {
                String key = e.getKey();
                if (key == null || key.isEmpty()) {
                    continue;
                }
                String value = e.getValue() == null ? "" : e.getValue();
                result = result.replace("{" + key + "}", value);
            }
        }

        warnUnresolvedTokens(template, result, templateVars);
        return result;
    }

    /**
     * 치환 후에도 {...} 가 남아 있으면 어떤 토큰이 누락됐는지 WARN.
     * - 원본 template 자체에 {...} 가 없었으면 호출 비용을 피한다.
     * - 운영자가 정책 본문에 의도적으로 중괄호 (예: 안내문 "{예시: ...}") 를 넣어둔 경우엔 false-positive 로그가 발생할 수 있다 — 코드/시더 검토 트리거 용도이므로 허용.
     */
    private void warnUnresolvedTokens(String template, String rendered, Map<String, String> templateVars) {
        if (template.indexOf('{') < 0) {
            return;
        }
        Matcher m = UNRESOLVED_TOKEN_PATTERN.matcher(rendered);
        Set<String> unresolved = null;
        while (m.find()) {
            String token = m.group(1).trim();
            if (token.isEmpty()) {
                continue;
            }
            if (unresolved == null) {
                unresolved = new LinkedHashSet<>();
            }
            unresolved.add(token);
        }
        if (unresolved == null) {
            return;
        }
        log.warn("Unresolved notification template tokens: {} (supplied keys: {})",
                unresolved,
                templateVars == null ? Set.of() : templateVars.keySet());
    }
}
