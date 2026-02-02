package it.ticketflow.ticketflow.entity;

import it.ticketflow.ticketflow.entity.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String ticketCode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TicketStatus status = TicketStatus.VALID;

    // Relazione: Molti Ticket appartengono a un Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Relazione: Molti Ticket fanno riferimento a un EventSector
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_sector_id", nullable = false)
    private EventSector eventSector;

    // === Metodi Helper ===

    @PrePersist
    public void generateTicketCode() {
        if (ticketCode == null) {
            ticketCode = "TKT-" + UUID.randomUUID().toString().toUpperCase();
        }
    }
}