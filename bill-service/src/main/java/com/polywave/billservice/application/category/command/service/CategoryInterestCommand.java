package com.polywave.billservice.application.category.command.service;

import java.util.List;

public record CategoryInterestCommand(
        Long userId,
        List<Long> categoryIds
) {}