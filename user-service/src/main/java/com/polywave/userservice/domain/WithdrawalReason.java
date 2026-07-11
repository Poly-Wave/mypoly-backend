package com.polywave.userservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원 탈퇴 사유. 탈퇴 화면에서 다중 선택 가능하다.
 * description 은 화면 노출 문구와 일치시킨다.
 */
@Getter
@RequiredArgsConstructor
public enum WithdrawalReason {
    INFREQUENT_USE("사용하는 빈도가 낮아요"),
    MISSING_FEATURE("원하는 기능이 없어요"),
    HARD_TO_USE("사용방법이 어렵고 불편해요"),
    LOW_QUALITY("결과물 품질이 기대와 달라요"),
    USING_ALTERNATIVE("다른 유사 서비스를 이용해요"),
    ETC("기타");

    private final String description;
}
