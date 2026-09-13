package com.event_service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(
    @NotNull String title,
    String description,
    @NotNull Long categoryId,
    @NotNull String venueName,
    @NotNull String address,
    @NotNull String city,
    @NotNull LocalDateTime startTime,
    @NotNull LocalDateTime endTime,
    @NotNull List<TierRequestDto> tierRequests
) {}

