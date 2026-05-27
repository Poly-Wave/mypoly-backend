package com.polywave.userservice.api.controller.internal;

import com.polywave.userservice.api.dto.UserNicknameResponse;
import com.polywave.userservice.application.user.query.service.UserLookupQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 다른 서비스가 사용자 정보를 lookup 할 때 사용하는 internal API.
 *
 * - InternalApiKeyFilter 가 X-Internal-Api-Key 헤더로 가드한다.
 * - 사용자 JWT 가 없는 컨텍스트(스케줄러 등) 에서 호출된다.
 */
@Tag(name = "Internal User Lookup", description = "[Internal] 사용자 정보 lookup")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/lookup")
public class InternalUserLookupController {

    private final UserLookupQueryService userLookupQueryService;

    @Operation(summary = "[Internal] 사용자 닉네임 일괄 조회", description = """
            주어진 userId 목록의 닉네임을 일괄 반환한다. 존재하지 않는 id 는 응답에서 제외된다.
            알림 본문 {별명} 변수 치환 등에 사용한다.
            """)
    @GetMapping("/by-ids")
    public ResponseEntity<List<UserNicknameResponse>> getNicknamesByIds(
            @Parameter(in = ParameterIn.HEADER, name = "X-Internal-Api-Key", description = "서비스 간 internal 공유 키", required = true)
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String internalApiKey,

            @Parameter(description = "조회할 사용자 ID 목록 (콤마 구분)", example = "1,2,3")
            @RequestParam("ids") Set<Long> ids
    ) {
        List<UserNicknameResponse> response = userLookupQueryService.findNicknamesByUserIds(ids).stream()
                .map(UserNicknameResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
