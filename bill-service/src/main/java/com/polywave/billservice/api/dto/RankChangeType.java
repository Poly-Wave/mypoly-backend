package com.polywave.billservice.api.dto;

/**
 * 인기 안건의 직전 배치 대비 순위 변동 유형.
 */
public enum RankChangeType {
    /** 상승 (현재 순위가 이전보다 높아짐) */
    UP,
    /** 하락 (현재 순위가 이전보다 낮아짐) */
    DOWN,
    /** 유지 (이전 배치와 동일한 순위) */
    SAME,
    /** 신규 진입 (이전 배치에 없었음) */
    NEW
}
