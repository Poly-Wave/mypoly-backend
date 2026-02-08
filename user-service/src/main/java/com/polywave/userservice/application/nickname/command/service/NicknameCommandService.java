package com.polywave.userservice.application.nickname.command.service;

public interface NicknameCommandService {

    /**
     * 사용자 닉네임을 설정(또는 변경)합니다.
     * - 중복 닉네임은 허용되지 않습니다.
     * - 금칙어가 포함된 닉네임은 허용되지 않습니다.
     */
    void assignNickname(Long userId, String nickname);
}
