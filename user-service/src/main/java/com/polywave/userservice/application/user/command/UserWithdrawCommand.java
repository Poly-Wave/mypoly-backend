package com.polywave.userservice.application.user.command;

import com.polywave.userservice.domain.WithdrawalReason;
import java.util.List;

public record UserWithdrawCommand(
        List<WithdrawalReason> reasons,
        String etcText
) {
}
