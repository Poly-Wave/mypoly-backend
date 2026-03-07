package com.polywave.userservice.application.nickname.query.result;

public record NicknameAvailabilityResult(
                boolean available,
                NicknameAvailabilityStatus status) {
}
