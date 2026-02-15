package com.polywave.userservice.application.user.command.service;

import com.polywave.userservice.application.user.command.UserUpdateProfileCommmand;

public interface UserCommandService {
    void updateUserProfile(Long userId, UserUpdateProfileCommmand command);
}
