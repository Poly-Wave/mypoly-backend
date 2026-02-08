package com.polywave.userservice.api.exception;

public class ForbiddenNicknameException extends RuntimeException {
    public ForbiddenNicknameException() {
        super("금칙어가 포함된 닉네임입니다.");
    }
}
