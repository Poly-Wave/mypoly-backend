package com.polywave.userservice.api.dto;

public record RandomNicknameResponse(
        String nickname
) {
    public static RandomNicknameResponse of(String nickname) {
        return new RandomNicknameResponse(nickname);
    }
}
