package com.polywave.userservice.application.user.query.service;

import com.polywave.userservice.application.user.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.user.query.result.RandomNicknameResult;

public interface NicknameQueryService {
    NicknameAvailabilityResult isNicknameAvailable(String nickname);
    RandomNicknameResult generateRandomNickname();
}
