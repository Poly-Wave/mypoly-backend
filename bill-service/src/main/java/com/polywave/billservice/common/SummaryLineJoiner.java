package com.polywave.billservice.common;

import java.util.ArrayList;
import java.util.List;

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

    public static List<String> toLines(String... lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line != null && !line.isBlank()) {
                result.add(line.strip());
            }
        }
        return result;
    }
}
