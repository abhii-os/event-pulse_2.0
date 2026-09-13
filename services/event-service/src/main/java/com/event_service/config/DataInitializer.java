package com.event_service.config;

import com.event_service.entity.EventCategory;
import com.event_service.entity.TicketTierType;
import com.event_service.repository.EventCategoryRepository;
import com.event_service.repository.TicketTierTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EventCategoryRepository categoryRepository;
    private final TicketTierTypeRepository ticketTierTypeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (ticketTierTypeRepository.count() == 0 && categoryRepository.count() == 0) {
            seedInitialData();
        }
    }

    private void seedInitialData() {
        // 1. Create Ticket Tier Types with layout defaults
        TicketTierType regular = TicketTierType.builder()
                .name("Regular")
                .defaultSeatsPerRow(15)
                .rowPrefix("REG-")
                .build();

        TicketTierType comfort = TicketTierType.builder()
                .name("Comfort")
                .defaultSeatsPerRow(12)
                .rowPrefix("COMF-")
                .build();

        TicketTierType recliner = TicketTierType.builder()
                .name("Recliner")
                .defaultSeatsPerRow(8)
                .rowPrefix("REC-")
                .build();

        TicketTierType general = TicketTierType.builder()
                .name("General")
                .defaultSeatsPerRow(20)
                .rowPrefix("GEN-")
                .build();

        TicketTierType vip = TicketTierType.builder()
                .name("VIP")
                .defaultSeatsPerRow(10)
                .rowPrefix("VIP-")
                .build();

        TicketTierType premium = TicketTierType.builder()
                .name("Premium")
                .defaultSeatsPerRow(12)
                .rowPrefix("PREM-")
                .build();

        // Save Tiers
        regular = ticketTierTypeRepository.save(regular);
        comfort = ticketTierTypeRepository.save(comfort);
        recliner = ticketTierTypeRepository.save(recliner);
        general = ticketTierTypeRepository.save(general);
        vip = ticketTierTypeRepository.save(vip);
        premium = ticketTierTypeRepository.save(premium);

        // 2. Map Tiers to Event Categories
        EventCategory movieTheater = EventCategory.builder()
                .name("Movie/Theatre/Shows")
                .allowedTierTypes(Set.of(regular, comfort, recliner))
                .build();

        EventCategory sportsConcerts = EventCategory.builder()
                .name("Sports/Concerts")
                .allowedTierTypes(Set.of(general, vip, premium))
                .build();

        EventCategory conference = EventCategory.builder()
                .name("Conference")
                .allowedTierTypes(Set.of(general, vip))
                .build();

        // Save Categories
        categoryRepository.save(movieTheater);
        categoryRepository.save(sportsConcerts);
        categoryRepository.save(conference);

        System.out.println(">>> Database seeded successfully with default categories and ticket tiers.");
    }
}