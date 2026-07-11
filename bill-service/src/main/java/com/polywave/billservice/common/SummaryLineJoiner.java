package com.polywave.billservice.common;

public final class SummaryLineJoiner {

    private SummaryLineJoiner() {
    }

    public static String join(String... lines) {
        StringBuilder joined = new StringBuilder();
        for (String line : lines) {
            if (line != null && !line.isBlank()) {
                if (joined.length() > 0) {
                    joined.append(' ');
                }
                joined.append(line.strip());
            }
        }
        return joined.toString();
    }
}
