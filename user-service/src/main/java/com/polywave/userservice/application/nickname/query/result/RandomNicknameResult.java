package com.polywave.userservice.application.nickname.query.result;

public record RandomNicknameResult(
        String nickname
) {
    public static RandomNicknameResult of(String nickname) {
        return new RandomNicknameResult(nickname);
    }
}
