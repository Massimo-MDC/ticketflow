package it.ticketflow.ticketflow.dto.venue;

import it.ticketflow.ticketflow.entity.Venue;

public record VenueResponseDTO(
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
        String imageUrl
) {
    public static VenueResponseDTO fromEntity(Venue venue) {
        return new VenueResponseDTO(
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
                venue.getImageUrl()
        );
    }
}