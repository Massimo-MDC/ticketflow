package it.ticketflow.ticketflow.dto.sector;

import it.ticketflow.ticketflow.entity.Sector;

import java.math.BigDecimal;

public record SectorResponseDTO(
        Long id,
        String name,
        Integer capacity,
        BigDecimal basePrice,
        String description,
        Long venueId,
        String venueName
) {
    public static SectorResponseDTO fromEntity(Sector sector) {
        return new SectorResponseDTO(
                sector.getId(),
                sector.getName(),
                sector.getCapacity(),
                sector.getBasePrice(),
                sector.getDescription(),
                sector.getVenue().getId(),
                sector.getVenue().getName()
        );
    }
}