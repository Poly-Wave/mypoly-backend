package com.polywave.userservice.api.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.polywave.userservice.annotation.LoginUser;
import com.polywave.userservice.api.dto.ApiResponse;
import com.polywave.userservice.api.dto.NicknameAvailabilityRequest;
import com.polywave.userservice.api.dto.NicknameAvailabilityResponse;
import com.polywave.userservice.api.dto.NicknameCreateRequest;
import com.polywave.userservice.application.user.command.service.UserCommandService;
import com.polywave.userservice.application.user.query.service.UserQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @GetMapping("/nicknames/availability")
    public ResponseEntity<ApiResponse<NicknameAvailabilityResponse>> checkNicknameAvailability(
            @Valid NicknameAvailabilityRequest request
    ) {
        boolean available = userQueryService.isNicknameAvailable(request.nickname());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(NicknameAvailabilityResponse.of(available)));
    }

    @PostMapping("/nicknames")
    public ResponseEntity<ApiResponse<Void>> createNickname(
            @Valid @RequestBody NicknameCreateRequest request, @LoginUser Long userId
    ) {
        userCommandService.createNickname(userId, request.nickname());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("닉네임이 생성 성공"));
    }

}
