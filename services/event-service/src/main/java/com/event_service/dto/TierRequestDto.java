package com.event_service.dto;

import jakarta.validation.constraints.NotNull;

public record TierRequestDto(
    @NotNull Long ticketTierTypeId,
    @NotNull Double price,
    @NotNull Integer quantity
) {}
