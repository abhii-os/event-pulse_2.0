package com.event_service.service;

import com.event_service.dto.*;
import com.event_service.entity.*;
import com.event_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;
    private final TicketTierTypeRepository ticketTierTypeRepository;
    private final EventTicketTierRepository eventTicketTierRepository;
    private final EventSeatRepository eventSeatRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public Long createEventWithDynamicLayout(CreateEventRequest request) {
        // 1. Fetch category
        EventCategory category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID"));

        // 2. Fetch or create venue inline
        Venue venue = venueRepository.findByAddressAndCity(request.address(), request.city())
            .orElseGet(() -> venueRepository.save(
                Venue.builder()
                    .name(request.venueName())
                    .address(request.address())
                    .city(request.city())
                    .build()
            ));

        // 3. Save main Event record
        Event event = Event.builder()
            .title(request.title())
            .description(request.description())
            .category(category)
            .venue(venue)
            .startTime(request.startTime())
            .endTime(request.endTime())
            .build();

        Event savedEvent = eventRepository.save(event);
        List<EventSeat> seatsToSave = new ArrayList<>();

        // 4. Iterate and build tier layout
        for (TierRequestDto tierReq : request.tierRequests()) {
            TicketTierType tierType = ticketTierTypeRepository.findById(tierReq.ticketTierTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Ticket Tier Type ID"));

            EventTicketTier eventTier = EventTicketTier.builder()
                .event(savedEvent)
                .ticketTierType(tierType)
                .price(tierReq.price())
                .totalQuantity(tierReq.quantity())
                .availableQuantity(tierReq.quantity())
                .build();

            eventTicketTierRepository.save(eventTier);

            // Generate seats dynamically
            generateSeats(savedEvent, eventTier, tierType, tierReq.quantity(), seatsToSave);
        }

        // 5. Batch save seats
        eventSeatRepository.saveAll(seatsToSave);

        return savedEvent.getId();
    }

    private void generateSeats(Event event, EventTicketTier tier, TicketTierType tierType, int totalQty, List<EventSeat> seatsList) {
        int seatsPerRow = tierType.getDefaultSeatsPerRow() != null ? tierType.getDefaultSeatsPerRow() : 10;
        String prefix = tierType.getRowPrefix() != null ? tierType.getRowPrefix() : "R";

        int remaining = totalQty;
        int rowIndex = 1;

        while (remaining > 0) {
            int currentChunk = Math.min(seatsPerRow, remaining);
            String rowLabel = prefix + rowIndex;

            for (int s = 1; s <= currentChunk; s++) {
                seatsList.add(EventSeat.builder()
                    .event(event)
                    .ticketTier(tier)
                    .rowNumber(rowLabel)
                    .seatNumber(rowLabel + "-S" + s)
                    .isBooked(false)
                    .build());
            }

            remaining -= currentChunk;
            rowIndex++;
        }
    }

    @Transactional(readOnly = true)
    public FullEventDetailResponse getFullEventDetails(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        VenueDto venueDto = new VenueDto(
                event.getVenue().getId(),
                event.getVenue().getName(),
                event.getVenue().getAddress(),
                event.getVenue().getCity()
        );

        List<EventTicketTier> tiers = eventTicketTierRepository.findByEventId(eventId);

        List<AvailableTierSummaryDto> tierSummaries = tiers.stream()
                .map(tier -> new AvailableTierSummaryDto(
                        tier.getTicketTierType().getId(),
                        tier.getTicketTierType().getName(),
                        tier.getPrice(),
                        tier.getTotalQuantity(),
                        tier.getAvailableQuantity()
                ))
                .toList();

        return new FullEventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCategory().getName(),
                venueDto,
                tierSummaries
        );
    }
    @Transactional(readOnly = true)
    public SeatingLayoutResponse getEventSeatingLayout(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        List<EventSeat> allSeats = eventSeatRepository.findByEventId(eventId);
        List<EventTicketTier> tiers = eventTicketTierRepository.findByEventId(eventId);

        // Group seats by tier -> row
        Map<Long, Map<String, List<EventSeat>>> seatsByTierAndRow = allSeats.stream()
            .collect(Collectors.groupingBy(
                seat -> seat.getTicketTier().getId(),
                LinkedHashMap::new,
                Collectors.groupingBy(EventSeat::getRowNumber, LinkedHashMap::new, Collectors.toList())
            ));

        List<TierLayoutDto> tierDtos = tiers.stream().map(tier -> {
            Map<String, List<EventSeat>> rowMap = seatsByTierAndRow.getOrDefault(tier.getId(), Collections.emptyMap());

            List<RowLayoutDto> rowDtos = rowMap.entrySet().stream().map(entry -> {
                List<SeatDto> seatDtos = entry.getValue().stream()
                    .map(seat -> new SeatDto(seat.getId(), seat.getSeatNumber(), seat.getIsBooked()))
                    .toList();
                return new RowLayoutDto(entry.getKey(), seatDtos);
            }).toList();

            return new TierLayoutDto(tier.getTicketTierType().getName(), tier.getPrice(), rowDtos);
        }).toList();

        return new SeatingLayoutResponse(event.getId(), event.getTitle(), event.getVenue().getName(), tierDtos);
    }
}