package com.polywave.billservice.application.category.command.service;

import java.util.HashSet;
import java.util.Set;

public record InterestDiff(
        Set<Long> toAdd,
        Set<Long> toRemove
) {
    public static InterestDiff of(Set<Long> current, Set<Long> requested) {
        Set<Long> toAdd = new HashSet<>(requested);
        toAdd.removeAll(current);

        Set<Long> toRemove = new HashSet<>(current);
        toRemove.removeAll(requested);

        return new InterestDiff(toAdd, toRemove);
    }
}