package com.event_service.dto;

import com.event_service.entity.Venue;

import java.time.LocalDateTime;
import java.util.List;

public record FullEventDetailResponse(
    Long eventId,
    String title,
    String description,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String categoryName,
    VenueDto venue,
    List<AvailableTierSummaryDto> availableTiers
) {}

