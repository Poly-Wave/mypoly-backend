package com.polywave.userservice.application.nickname.query.service;

import com.polywave.userservice.application.nickname.query.result.NicknameAvailabilityResult;
import com.polywave.userservice.application.nickname.query.result.RandomNicknameResult;

public interface NicknameQueryService {
    NicknameAvailabilityResult isNicknameAvailable(String nickname);
    RandomNicknameResult generateRandomNickname();
}
