package com.polywave.billservice.api.controller.internal;

import com.polywave.billservice.application.user.command.service.UserBillDataCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user-service 회원 탈퇴 시 호출하는 사용자 파생 데이터 삭제 internal API.
 *
 * - InternalApiKeyFilter 가 X-Internal-Api-Key 헤더로 가드한다.
 * - 북마크/관심사/조회 기록만 삭제하고 표결(user_bill_votes)은 보존한다. 멱등.
 */
@Tag(name = "Internal User Bill Data", description = "[Internal] 회원 탈퇴 시 사용자 의안 파생 데이터 삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/users")
public class InternalUserBillDataController {

    private final UserBillDataCommandService userBillDataCommandService;

    @Operation(summary = "[Internal] 사용자 의안 파생 데이터 삭제(북마크/관심사/조회)", description = """
            회원 탈퇴 시 해당 사용자의 북마크/관심사/조회 기록을 삭제한다.
            표결(투표) 기록은 표결 결과 보존을 위해 삭제하지 않는다. 멱등하게 동작한다.
            """)
    @DeleteMapping("/{userId}/bill-data")
    public ResponseEntity<Void> deleteUserBillData(
            @Parameter(in = ParameterIn.HEADER, name = "X-Internal-Api-Key", description = "서비스 간 internal 공유 키", required = true)
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String internalApiKey,

            @Parameter(description = "탈퇴 사용자 ID") @PathVariable Long userId
    ) {
        userBillDataCommandService.deleteAllByUser(userId);
        return ResponseEntity.noContent().build();
    }
}
