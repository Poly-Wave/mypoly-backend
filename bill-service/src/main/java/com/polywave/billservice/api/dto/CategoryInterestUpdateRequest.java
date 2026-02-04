package com.polywave.billservice.api.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CategoryInterestUpdateRequest(
        @NotEmpty
        List<Long> categoryIds
) {
}
