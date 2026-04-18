package com.polywave.billservice.domain;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public enum AgeBand {
    TEN,
    TWENTY,
    THIRTY,
    FORTY,
    FIFTY,
    SIXTY_PLUS;

    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    public static AgeBand fromBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isBlank()) {
            throw new IllegalArgumentException("birthDate is required");
        }

        try {
            LocalDate parsed = LocalDate.parse(birthDate, BIRTH_DATE_FORMATTER);
            int age = Period.between(parsed, LocalDate.now()).getYears();
            return fromAge(age);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("birthDate must be yyyyMMdd format", e);
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
