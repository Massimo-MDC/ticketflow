package it.ticketflow.ticketflow.dto.event;

import it.ticketflow.ticketflow.entity.*;
import it.ticketflow.ticketflow.entity.enums.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record EventResponseDTO(
        Long id,
        String title,
        String description,
        LocalDate eventDate,
        LocalTime eventTime,
        String imageUrl,
        EventStatus status,
        String eventType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        // Venue info
        Long venueId,
        String venueName,
        String venueCity,
        // Category info
        Long categoryId,
        String categoryName,
        // Organizer info
        UUID organizerId,
        String organizerName,
        // Campi specifici per tipo
        String genre,
        String tourName,
        String homeTeam,
        String awayTeam,
        String league,
        String director,
        Integer durationMinutes,
        String ageRestriction
) {
    public static EventResponseDTO fromEntity(Event event) {
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

        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getEventTime(),
                event.getImageUrl(),
                event.getStatus(),
                event.getEventType(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                event.getVenue().getId(),
                event.getVenue().getName(),
                event.getVenue().getCity(),
                event.getCategory().getId(),
                event.getCategory().getName(),
                event.getOrganizer().getId(),
                event.getOrganizer().getFullName(),
                genre, tourName,
                homeTeam, awayTeam, league,
                director, durationMinutes, ageRestriction
        );
    }
}