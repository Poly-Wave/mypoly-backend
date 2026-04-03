package com.polywave.userservice.application.user.command;

import com.polywave.userservice.domain.Gender;

public record UserUpdateBasicProfileCommand(
        String nickname,
        Gender gender,
        String birthDate,
        String sido,
        String sigungu,
        String emdName
) {
}