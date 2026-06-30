package com.polywave.userservice.api.dto;

import com.polywave.userservice.domain.WithdrawalReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "회원 탈퇴 요청")
public record UserWithdrawRequest(
        @Schema(
                description = "탈퇴 사유(중복 선택). 가능 값: "
                        + "INFREQUENT_USE, MISSING_FEATURE, HARD_TO_USE, LOW_QUALITY, USING_ALTERNATIVE, ETC",
                example = "[\"INFREQUENT_USE\", \"ETC\"]"
        )
        List<WithdrawalReason> reasons,

        @Schema(description = "기타(ETC) 선택 시 직접 입력한 사유. 최대 200자.", example = "원하는 정치인 정보가 부족해요")
        @Size(max = 200, message = "기타 사유는 200자 이하여야 합니다.")
        String etcText
) {
}
