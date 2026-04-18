package com.polywave.billservice.application.bill.query.service;

import java.util.Arrays;
import java.util.Optional;

public enum BillUiStage {
    RECEIVED("RECEIVED", "접수", 1),
    REVIEW("REVIEW", "심사", 2),
    DECISION("DECISION", "의결", 3),
    COMPLETED("COMPLETED", "완료", 4);

    private final String code;
    private final String displayName;
    private final int order;

    BillUiStage(String code, String displayName, int order) {
        this.code = code;
        this.displayName = displayName;
        this.order = order;
    }

    public static BillUiStage fromProcStageOrder(Integer currentProcStageOrder) {
        if (currentProcStageOrder == null || currentProcStageOrder <= 1) {
            return RECEIVED;
        }
        if (currentProcStageOrder == 2) {
            return REVIEW;
        }
        if (currentProcStageOrder == 3) {
            return DECISION;
        }
        return COMPLETED;
    }

    public static Optional<BillUiStage> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(stage -> stage.code.equalsIgnoreCase(code.trim()))
                .findFirst();
    }

    public String code() {
        return code;
    }

    public String displayName() {
        return displayName;
    }

    public int order() {
        return order;
    }
}