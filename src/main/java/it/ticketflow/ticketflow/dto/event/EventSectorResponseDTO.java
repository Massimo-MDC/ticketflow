package it.ticketflow.ticketflow.dto.event;

import it.ticketflow.ticketflow.entity.EventSector;

import java.math.BigDecimal;

public record EventSectorResponseDTO(
        Long id,
        Long sectorId,
        String sectorName,
        Integer availableTickets,
        BigDecimal currentPrice,
        Integer totalCapacity
) {
    public static EventSectorResponseDTO fromEntity(EventSector eventSector) {
        return new EventSectorResponseDTO(
                eventSector.getId(),
                eventSector.getSector().getId(),
                eventSector.getSector().getName(),
                eventSector.getAvailableTickets(),
                eventSector.getCurrentPrice(),
                eventSector.getSector().getCapacity()
        );
    }
}