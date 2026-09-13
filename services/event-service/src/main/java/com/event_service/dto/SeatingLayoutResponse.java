package com.event_service.dto;

import java.util.List;

public record SeatingLayoutResponse(
    Long eventId,
    String eventTitle,
    String venueName,
    List<TierLayoutDto> seatingLayout
) {}

