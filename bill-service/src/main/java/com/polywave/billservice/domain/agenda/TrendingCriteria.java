package com.polywave.billservice.domain.agenda;

/**
 * "요즘 핫한" 탭 기준: 최근 N일 내 투표 완료 수로 정렬.
 */
public final class TrendingCriteria {

    /** 집계 기간(일) */
    public static final int DAYS = 7;

    private TrendingCriteria() {
    }
}
