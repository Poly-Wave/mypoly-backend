package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.api.dto.UserUpdateProfileRequest;

public interface UserCommandService {
    void updateUserProfile(Long userId, UserUpdateProfileRequest request);
}
