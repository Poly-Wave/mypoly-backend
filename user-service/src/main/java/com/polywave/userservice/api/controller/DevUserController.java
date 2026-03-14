package com.polywave.userservice.api.controller;

import com.polywave.security.annotation.LoginUser;
import com.polywave.userservice.api.spec.DevUserApi;
import com.polywave.userservice.application.user.command.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev-users")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "user.dev-delete.enabled", havingValue = "true")
public class DevUserController implements DevUserApi {

    private final UserCommandService userCommandService;

    @Override
    public ResponseEntity<Void> deleteMe(@LoginUser Long userId) {
        userCommandService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}