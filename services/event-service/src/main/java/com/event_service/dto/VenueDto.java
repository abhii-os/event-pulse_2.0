package com.event_service.dto;

import jakarta.validation.constraints.NotBlank;

public record VenueDto(
    Long id,
    
    @NotBlank(message = "Venue name is required")
    String name,
    
    @NotBlank(message = "Venue address is required")
    String address,
    
    @NotBlank(message = "City is required")
    String city
) {}