package com.event_service.dto;

import java.util.List;

public record TierLayoutDto(
    String tierName,
    Double price,
    List<RowLayoutDto> rows
) {}
