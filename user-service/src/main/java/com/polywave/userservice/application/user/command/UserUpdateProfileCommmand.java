package com.polywave.userservice.application.user.command;

import com.polywave.userservice.api.dto.UserUpdateProfileRequest;
import com.polywave.userservice.domain.Gender;

public record UserUpdateProfileCommmand(
        Gender gender,
        String birthdate,
        String sido,
        String sigungu,
        String emdName
) {
}
