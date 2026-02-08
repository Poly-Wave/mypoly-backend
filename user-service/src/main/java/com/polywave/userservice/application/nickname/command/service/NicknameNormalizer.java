package com.polywave.userservice.application.nickname.policy;

import java.util.regex.Pattern;

public final class NicknameNormalizer {

    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");

    private NicknameNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        return MULTI_SPACE.matcher(trimmed).replaceAll(" ");
    }
}
