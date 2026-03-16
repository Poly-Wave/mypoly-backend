package com.polywave.billservice.domain.agenda;

import java.util.Arrays;

/**
 * 안건 탭 종류.
 * 새 탭 추가 시 여기와 해당 QueryService, Controller switch에 반영 필요.
 */
public enum AgendaTabType {

    HOT_DEBATE("hot_debate", "쟁쟁한", "찬반이 팽팽한 의안", 1),
    PERSONALIZED("personalized", "맞춤형", "내 관심사 기반 추천", 2),
    TRENDING("trending", "요즘 핫한", "최근 급상승 의안", 3);

    private final String code;
    private final String label;
    private final String description;
    private final int displayOrder;

    AgendaTabType(String code, String label, String description, int displayOrder) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public static AgendaTabType fromCode(String code) {
        return Arrays.stream(values())
                .filter(t -> t.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown agenda tab code: " + code));
    }
}
