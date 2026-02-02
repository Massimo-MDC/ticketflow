package it.ticketflow.ticketflow.dto.ticket;

import it.ticketflow.ticketflow.entity.Ticket;
import it.ticketflow.ticketflow.entity.enums.TicketStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketResponseDTO(
        UUID id,
        String ticketCode,
        BigDecimal purchasePrice,
        TicketStatus status,
        String eventTitle,
        String sectorName,
        String venueName
) {
    public static TicketResponseDTO fromEntity(Ticket ticket) {
        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getTicketCode(),
                ticket.getPurchasePrice(),
                ticket.getStatus(),
                ticket.getEventSector().getEvent().getTitle(),
                ticket.getEventSector().getSector().getName(),
                ticket.getEventSector().getEvent().getVenue().getName()
        );
    }
}