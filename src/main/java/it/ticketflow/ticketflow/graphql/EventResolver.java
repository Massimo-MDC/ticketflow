package it.ticketflow.ticketflow.graphql;

import it.ticketflow.ticketflow.entity.*;
import it.ticketflow.ticketflow.repository.EventArtistRepository;
import it.ticketflow.ticketflow.repository.EventSectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventResolver {

    private final EventSectorRepository eventSectorRepository;
    private final EventArtistRepository eventArtistRepository;

    @SchemaMapping(typeName = "Event", field = "eventSectors")
    public List<EventSector> getEventSectors(Event event) {
        return eventSectorRepository.findByEventId(event.getId());
    }

    @SchemaMapping(typeName = "Event", field = "eventArtists")
    public List<EventArtist> getEventArtists(Event event) {
        return eventArtistRepository.findByEventId(event.getId());
    }

    @SchemaMapping(typeName = "Event", field = "eventType")
    public String getEventType(Event event) {
        if (event instanceof ConcertEvent) return "CONCERT";
        if (event instanceof SportEvent) return "SPORT";
        if (event instanceof TheaterEvent) return "THEATER";
        return "UNKNOWN";
    }

    @SchemaMapping(typeName = "Event", field = "genre")
    public String getGenre(Event event) {
        if (event instanceof ConcertEvent concertEvent) {
            return concertEvent.getGenre();
        }
        return null;
    }

    @SchemaMapping(typeName = "Event", field = "tourName")
    public String getTourName(Event event) {
        if (event instanceof ConcertEvent concertEvent) {
            return concertEvent.getTourName();
        }
        return null;
    }

    @SchemaMapping(typeName = "Event", field = "homeTeam")
    public String getHomeTeam(Event event) {
        if (event instanceof SportEvent sportEvent) {
            return sportEvent.getHomeTeam();
        }
        return null;
    }

    @SchemaMapping(typeName = "Event", field = "awayTeam")
    public String getAwayTeam(Event event) {
        if (event instanceof SportEvent sportEvent) {
            return sportEvent.getAwayTeam();
        }
        return null;
    }

    @SchemaMapping(typeName = "Event", field = "league")
    public String getLeague(Event event) {
        if (event instanceof SportEvent sportEvent) {
            return sportEvent.getLeague();
        }
        return null;
    }

    @SchemaMapping(typeName = "Event", field = "director")
    public String getDirector(Event event) {
        if (event instanceof TheaterEvent theaterEvent) {
            return theaterEvent.getDirector();
        }
        return null;
    }

    @SchemaMapping(typeName = "Event", field = "durationMinutes")
    public Integer getDurationMinutes(Event event) {
        if (event instanceof TheaterEvent theaterEvent) {
            return theaterEvent.getDurationMinutes();
        }
        return null;
    }

    @SchemaMapping(typeName = "Event", field = "ageRestriction")
    public String getAgeRestriction(Event event) {
        if (event instanceof TheaterEvent theaterEvent) {
            return theaterEvent.getAgeRestriction();
        }
        return null;
    }
}