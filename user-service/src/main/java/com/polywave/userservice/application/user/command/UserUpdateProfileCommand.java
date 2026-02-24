package com.polywave.userservice.application.user.command;

import com.polywave.userservice.domain.Gender;

public record UserUpdateProfileCommand(
        Gender gender,
        String birthdate,
        String sido,
        String sigungu,
        String emdName
) {
}
