package com.polywave.billservice.api.dto;

import com.polywave.billservice.domain.UserVoteResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserBillVoteRequest(
        @NotNull(message = "투표 결과는 필수입니다.")
        @Schema(description = "투표 결과", allowableValues = {"AGREE", "DISAGREE"}, example = "AGREE")
        UserVoteResult voteResult
) {
}
