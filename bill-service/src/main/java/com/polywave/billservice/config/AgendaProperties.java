package com.polywave.billservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bill.agenda")
public record AgendaProperties(
        Trending trending,
        HotDebate hotDebate
) {
    public record Trending(
            int days
    ) {
    }

    public record HotDebate(
            int days,
            int minVoteCount
    ) {
    }
}

