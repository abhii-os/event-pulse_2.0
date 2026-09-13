package com.event_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketTierType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., Regular, Comfort, Recliner, VIP
    private Integer defaultSeatsPerRow;
    private String rowPrefix; // e.g., REG-, COMF-, REC-
}