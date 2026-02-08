package com.polywave.userservice.application.user.query.result;

public record RandomNicknameResult(
        String nickname
) {
    public static RandomNicknameResult of(String nickname) {
        return new RandomNicknameResult(nickname);
    }
}
