package com.event_service.repository;

import com.event_service.entity.EventTicketTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTicketTierRepository extends JpaRepository<EventTicketTier, Long> {
    List<EventTicketTier> findByEventId(Long eventId);
}
