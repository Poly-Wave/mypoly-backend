package com.polywave.billservice.config.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 요청 파라미터 날짜를 KST 기준 {@link LocalDate}로 변환합니다.
 * <ul>
 *   <li>{@code 2026-05-13}</li>
 *   <li>{@code 2026-05-13T00:00:00.000Z} 등 ISO-8601 datetime</li>
 * </ul>
 */
@Component
public class FlexibleLocalDateConverter implements Converter<String, LocalDate> {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Override
    public LocalDate convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }

        String trimmed = source.trim();
        if (trimmed.length() == 10) {
            try {
                return LocalDate.parse(trimmed);
            } catch (DateTimeParseException ignored) {
                // fall through
            }
        }

        try {
            return Instant.parse(trimmed).atZone(KST).toLocalDate();
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        try {
            return OffsetDateTime.parse(trimmed).atZoneSameInstant(KST).toLocalDate();
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        try {
            return ZonedDateTime.parse(trimmed).withZoneSameInstant(KST).toLocalDate();
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        try {
            return LocalDateTime.parse(trimmed).atZone(KST).toLocalDate();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "날짜 형식이 올바르지 않습니다. 예: 2026-05-13 또는 2026-05-13T00:00:00.000Z",
                    e
            );
        }
    }
}
