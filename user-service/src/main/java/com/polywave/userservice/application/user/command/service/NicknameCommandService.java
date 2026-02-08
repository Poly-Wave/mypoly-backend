package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.application.user.query.result.NicknameAvailabilityResult;

public interface NicknameCommandService {
    void assignNickname(Long userId, String nickname, NicknameAvailabilityResult availabilityResult);
}
