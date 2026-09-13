package com.event_service.dto;

public record AvailableTierSummaryDto(
    Long ticketTierTypeId,
    String tierName,
    Double price,
    Integer totalQuantity,
    Integer availableQuantity
) {}
