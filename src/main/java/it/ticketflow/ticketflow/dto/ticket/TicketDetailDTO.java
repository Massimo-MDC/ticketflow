package it.ticketflow.ticketflow.dto.ticket;

import it.ticketflow.ticketflow.entity.Ticket;
import it.ticketflow.ticketflow.entity.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TicketDetailDTO(
        UUID id,
        String ticketCode,
        BigDecimal purchasePrice,
        TicketStatus status,
        // Event info
        Long eventId,
        String eventTitle,
        LocalDate eventDate,
        LocalTime eventTime,
        String eventImageUrl,
        // Sector info
        String sectorName,
        // Venue info
        String venueName,
        String venueAddress,
        String venueCity,
        // Order info
        UUID orderId,
        String orderNumber
) {
    public static TicketDetailDTO fromEntity(Ticket ticket) {
        var event = ticket.getEventSector().getEvent();
        var venue = event.getVenue();
        var sector = ticket.getEventSector().getSector();

        return new TicketDetailDTO(
                ticket.getId(),
                ticket.getTicketCode(),
                ticket.getPurchasePrice(),
                ticket.getStatus(),
                event.getId(),
                event.getTitle(),
                event.getEventDate(),
                event.getEventTime(),
                event.getImageUrl(),
                sector.getName(),
                venue.getName(),
                venue.getAddress(),
                venue.getCity(),
                ticket.getOrder().getId(),
                ticket.getOrder().getOrderNumber()
        );
    }
}