package com.event_service.dto;

import java.util.List;

public record RowLayoutDto(
    String rowName,
    List<SeatDto> seats
) {}
