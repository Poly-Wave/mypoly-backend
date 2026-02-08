package com.polywave.userservice.application.nickname.command.service;

import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;

public interface NicknameCommandService {
    void assignNickname(Long userId, String nickname, NicknameAvailabilityResult availabilityResult);
}
