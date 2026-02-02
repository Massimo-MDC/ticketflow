package it.ticketflow.ticketflow.dto.venue;

import it.ticketflow.ticketflow.dto.sector.SectorResponseDTO;
import it.ticketflow.ticketflow.entity.Venue;

import java.util.List;

public record VenueWithSectorsDTO(
        Long id,
        String name,
        String address,
        String city,
        String region,
        String country,
        String zipCode,
        Double latitude,
        Double longitude,
        Integer totalCapacity,
        String description,
        String imageUrl,
        List<SectorResponseDTO> sectors
) {
    public static VenueWithSectorsDTO fromEntity(Venue venue) {
        List<SectorResponseDTO> sectorDTOs = venue.getSectors().stream()
                .map(SectorResponseDTO::fromEntity)
                .toList();

        return new VenueWithSectorsDTO(
                venue.getId(),
                venue.getName(),
                venue.getAddress(),
                venue.getCity(),
                venue.getRegion(),
                venue.getCountry(),
                venue.getZipCode(),
                venue.getLatitude(),
                venue.getLongitude(),
                venue.getTotalCapacity(),
                venue.getDescription(),
                venue.getImageUrl(),
                sectorDTOs
        );
    }
}