package com.polywave.billservice.domain;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public enum AgeBand {
    TEN,
    TWENTY,
    THIRTY,
    FORTY,
    FIFTY,
    SIXTY_PLUS;

    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    /** API/비즈니스 날짜 기준을 KST로 통일 (application.properties jackson.time-zone 과 동일) */
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * user-service 생년월일(yyyyMMdd) 문자열로부터 연령대를 계산한다.
     *
     * @return 빈 값이거나 파싱 불가면 {@link Optional#empty()}
     */
    public static Optional<AgeBand> tryFromBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isBlank()) {
            return Optional.empty();
        }

        try {
            LocalDate parsed = LocalDate.parse(birthDate, BIRTH_DATE_FORMATTER);
            LocalDate today = LocalDate.now(BUSINESS_ZONE);
            int age = Period.between(parsed, today).getYears();
            return Optional.of(fromAge(age));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    private static AgeBand fromAge(int age) {
        if (age <= 19) {
            return TEN;
        }
        if (age <= 29) {
            return TWENTY;
        }
        if (age <= 39) {
            return THIRTY;
        }
        if (age <= 49) {
            return FORTY;
        }
        if (age <= 59) {
            return FIFTY;
        }
        return SIXTY_PLUS;
    }
}
