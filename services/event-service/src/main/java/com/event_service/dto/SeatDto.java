package com.event_service.dto;

public record SeatDto(
    Long seatId,
    String seatNumber,
    Boolean isBooked
) {}
