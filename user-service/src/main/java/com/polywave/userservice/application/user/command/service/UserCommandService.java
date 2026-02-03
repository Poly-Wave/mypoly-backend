package com.polywave.userservice.application.user.command.service;

public interface UserCommandService {
    void createNickname(Long userId, String nickname);
}
