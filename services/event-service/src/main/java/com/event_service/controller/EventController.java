package com.event_service.controller;

import com.event_service.dto.*;
import com.event_service.service.EventServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventServiceImpl eventService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        Long eventId = eventService.createEventWithDynamicLayout(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Event created successfully", "eventId", eventId));
    }

    @GetMapping("/{eventId}/seating-layout")
    public ResponseEntity<SeatingLayoutResponse> getSeatingLayout(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventSeatingLayout(eventId));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<FullEventDetailResponse> getEventDetails(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getFullEventDetails(eventId));
    }
}