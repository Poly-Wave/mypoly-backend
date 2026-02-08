package com.polywave.userservice.repository.query;

import com.polywave.userservice.domain.NicknameWordType;

public interface NicknameWordQueryRepository {
    String pickRandom(NicknameWordType nicknameWordType);
}
