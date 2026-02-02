package it.ticketflow.ticketflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event_sectors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer availableTickets;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentPrice;

    // Relazione: Molti EventSector appartengono a un Event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Relazione: Molti EventSector fanno riferimento a un Sector
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    // Relazione: Un EventSector ha molti Ticket
    @OneToMany(mappedBy = "eventSector", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    // === Metodi Helper ===

    public boolean hasAvailableTickets() {
        return availableTickets > 0;
    }

    public void decrementAvailableTickets() {
        if (availableTickets > 0) {
            availableTickets--;
        }
    }

    public void incrementAvailableTickets() {
        availableTickets++;
    }
}