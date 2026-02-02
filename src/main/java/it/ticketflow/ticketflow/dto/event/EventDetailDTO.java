package it.ticketflow.ticketflow.dto.event;

import it.ticketflow.ticketflow.entity.*;
import it.ticketflow.ticketflow.entity.enums.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record EventDetailDTO(
        Long id,
        String title,
        String description,
        LocalDate eventDate,
        LocalTime eventTime,
        String imageUrl,
        EventStatus status,
        String eventType,
        LocalDateTime createdAt,
        // Venue completa
        Long venueId,
        String venueName,
        String venueAddress,
        String venueCity,
        String venueCountry,
        // Category
        Long categoryId,
        String categoryName,
        // Organizer
        UUID organizerId,
        String organizerName,
        // Settori disponibili
        List<EventSectorResponseDTO> availableSectors,
        // Artisti (per concerti)
        List<EventArtistResponseDTO> artists,
        // Campi specifici
        String genre,
        String tourName,
        String homeTeam,
        String awayTeam,
        String league,
        String director,
        Integer durationMinutes,
        String ageRestriction
) {
    public static EventDetailDTO fromEntity(Event event,
                                            List<EventSectorResponseDTO> sectors,
                                            List<EventArtistResponseDTO> artists) {
        String genre = null, tourName = null;
        String homeTeam = null, awayTeam = null, league = null;
        String director = null;
        Integer durationMinutes = null;
        String ageRestriction = null;

        if (event instanceof ConcertEvent concert) {
            genre = concert.getGenre();
            tourName = concert.getTourName();
        } else if (event instanceof SportEvent sport) {
            homeTeam = sport.getHomeTeam();
            awayTeam = sport.getAwayTeam();
            league = sport.getLeague();
        } else if (event instanceof TheaterEvent theater) {
            director = theater.getDirector();
            durationMinutes = theater.getDurationMinutes();
            ageRestriction = theater.getAgeRestriction();
        }

        Venue venue = event.getVenue();

        return new EventDetailDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getEventTime(),
                event.getImageUrl(),
                event.getStatus(),
                event.getEventType(),
                event.getCreatedAt(),
                venue.getId(),
                venue.getName(),
                venue.getAddress(),
                venue.getCity(),
                venue.getCountry(),
                event.getCategory().getId(),
                event.getCategory().getName(),
                event.getOrganizer().getId(),
                event.getOrganizer().getFullName(),
                sectors,
                artists,
                genre, tourName,
                homeTeam, awayTeam, league,
                director, durationMinutes, ageRestriction
        );
    }
}