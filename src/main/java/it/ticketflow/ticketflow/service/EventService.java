package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.dto.event.*;
import it.ticketflow.ticketflow.entity.*;
import it.ticketflow.ticketflow.entity.enums.EventStatus;
import it.ticketflow.ticketflow.exception.BadRequestException;
import it.ticketflow.ticketflow.exception.NotFoundException;
import it.ticketflow.ticketflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventSectorRepository eventSectorRepository;
    private final EventArtistRepository eventArtistRepository;
    private final VenueService venueService;
    private final CategoryService categoryService;
    private final SectorService sectorService;
    private final ArtistService artistService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    // === Query Methods ===

    public Page<EventResponseDTO> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(EventResponseDTO::fromEntity);
    }

    public Page<EventResponseDTO> findPublished(Pageable pageable) {
        return eventRepository.findByStatus(EventStatus.PUBLISHED, pageable)
                .map(EventResponseDTO::fromEntity);
    }

    public Page<EventResponseDTO> findByCity(String city, Pageable pageable) {
        return eventRepository.findByVenueCity(city, pageable)
                .map(EventResponseDTO::fromEntity);
    }

    public Page<EventResponseDTO> findByCategory(Long categoryId, Pageable pageable) {
        return eventRepository.findByStatusAndCategoryId(EventStatus.PUBLISHED, categoryId, pageable)
                .map(EventResponseDTO::fromEntity);
    }

    public Page<EventResponseDTO> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return eventRepository.findByEventDateBetween(startDate, endDate, pageable)
                .map(EventResponseDTO::fromEntity);
    }

    public Page<EventResponseDTO> findByOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable)
                .map(EventResponseDTO::fromEntity);
    }

    public Page<EventResponseDTO> findAvailable(Pageable pageable) {
        return eventRepository.findAvailableEvents(EventStatus.PUBLISHED, pageable)
                .map(EventResponseDTO::fromEntity);
    }

    public Page<EventResponseDTO> search(String searchTerm, Pageable pageable) {
        return eventRepository.searchEvents(searchTerm, pageable)
                .map(EventResponseDTO::fromEntity);
    }

    public List<EventResponseDTO> findTopEvents(int limit) {
        return eventRepository.findTopEventsByTicketsSold(limit).stream()
                .map(EventResponseDTO::fromEntity)
                .toList();
    }

    public EventDetailDTO findById(Long id) {
        Event event = eventRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Event", id));

        List<EventSectorResponseDTO> sectors = eventSectorRepository.findAvailableSectorsByEventId(id)
                .stream()
                .map(EventSectorResponseDTO::fromEntity)
                .toList();

        List<EventArtistResponseDTO> artists = eventArtistRepository.findByEventIdWithArtists(id)
                .stream()
                .map(EventArtistResponseDTO::fromEntity)
                .toList();

        return EventDetailDTO.fromEntity(event, sectors, artists);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event", id));
    }

    // === Create Methods ===

    @Transactional
    public EventResponseDTO createConcertEvent(ConcertEventRequestDTO dto, UUID organizerId) {
        User organizer = userService.getUserById(organizerId);
        Venue venue = venueService.getVenueById(dto.venueId());
        Category category = categoryService.getCategoryById(dto.categoryId());

        ConcertEvent event = ConcertEvent.builder()
                .title(dto.title())
                .description(dto.description())
                .eventDate(dto.eventDate())
                .eventTime(dto.eventTime())
                .venue(venue)
                .category(category)
                .organizer(organizer)
                .genre(dto.genre())
                .tourName(dto.tourName())
                .build();

        Event savedEvent = eventRepository.save(event);
        return EventResponseDTO.fromEntity(savedEvent);
    }

    @Transactional
    public EventResponseDTO createSportEvent(SportEventRequestDTO dto, UUID organizerId) {
        User organizer = userService.getUserById(organizerId);
        Venue venue = venueService.getVenueById(dto.venueId());
        Category category = categoryService.getCategoryById(dto.categoryId());

        SportEvent event = SportEvent.builder()
                .title(dto.title())
                .description(dto.description())
                .eventDate(dto.eventDate())
                .eventTime(dto.eventTime())
                .venue(venue)
                .category(category)
                .organizer(organizer)
                .homeTeam(dto.homeTeam())
                .awayTeam(dto.awayTeam())
                .league(dto.league())
                .build();

        Event savedEvent = eventRepository.save(event);
        return EventResponseDTO.fromEntity(savedEvent);
    }

    @Transactional
    public EventResponseDTO createTheaterEvent(TheaterEventRequestDTO dto, UUID organizerId) {
        User organizer = userService.getUserById(organizerId);
        Venue venue = venueService.getVenueById(dto.venueId());
        Category category = categoryService.getCategoryById(dto.categoryId());

        TheaterEvent event = TheaterEvent.builder()
                .title(dto.title())
                .description(dto.description())
                .eventDate(dto.eventDate())
                .eventTime(dto.eventTime())
                .venue(venue)
                .category(category)
                .organizer(organizer)
                .director(dto.director())
                .durationMinutes(dto.durationMinutes())
                .ageRestriction(dto.ageRestriction())
                .build();

        Event savedEvent = eventRepository.save(event);
        return EventResponseDTO.fromEntity(savedEvent);
    }

    // === Update Methods ===

    @Transactional
    public EventResponseDTO updateStatus(Long id, EventStatus newStatus, UUID organizerId) {
        Event event = getEventById(id);
        verifyOwnership(event, organizerId);

        event.setStatus(newStatus);
        Event savedEvent = eventRepository.save(event);
        return EventResponseDTO.fromEntity(savedEvent);
    }

    @Transactional
    public EventResponseDTO uploadImage(Long id, MultipartFile file, UUID organizerId) {
        Event event = getEventById(id);
        verifyOwnership(event, organizerId);

        if (event.getImageUrl() != null) {
            cloudinaryService.deleteImage(event.getImageUrl());
        }

        String imageUrl = cloudinaryService.uploadImage(file, "events");
        event.setImageUrl(imageUrl);

        Event savedEvent = eventRepository.save(event);
        return EventResponseDTO.fromEntity(savedEvent);
    }

    // === Event Sectors ===

    @Transactional
    public EventSectorResponseDTO addEventSector(Long eventId, EventSectorRequestDTO dto, UUID organizerId) {
        Event event = getEventById(eventId);
        verifyOwnership(event, organizerId);

        Sector sector = sectorService.getSectorById(dto.sectorId());

        // Verifica che il settore appartenga alla venue dell'evento
        if (!sector.getVenue().getId().equals(event.getVenue().getId())) {
            throw new BadRequestException("Il settore non appartiene alla venue dell'evento");
        }

        EventSector eventSector = EventSector.builder()
                .event(event)
                .sector(sector)
                .availableTickets(dto.availableTickets())
                .currentPrice(dto.currentPrice())
                .build();

        EventSector savedEventSector = eventSectorRepository.save(eventSector);
        return EventSectorResponseDTO.fromEntity(savedEventSector);
    }

    @Transactional
    public void removeEventSector(Long eventId, Long eventSectorId, UUID organizerId) {
        Event event = getEventById(eventId);
        verifyOwnership(event, organizerId);

        EventSector eventSector = eventSectorRepository.findById(eventSectorId)
                .orElseThrow(() -> new NotFoundException("EventSector", eventSectorId));

        if (!eventSector.getEvent().getId().equals(eventId)) {
            throw new BadRequestException("Il settore non appartiene a questo evento");
        }

        eventSectorRepository.delete(eventSector);
    }

    // === Event Artists ===

    @Transactional
    public EventArtistResponseDTO addEventArtist(Long eventId, EventArtistRequestDTO dto, UUID organizerId) {
        Event event = getEventById(eventId);
        verifyOwnership(event, organizerId);

        Artist artist = artistService.getArtistById(dto.artistId());

        EventArtist eventArtist = EventArtist.builder()
                .event(event)
                .artist(artist)
                .isHeadliner(dto.isHeadliner() != null ? dto.isHeadliner() : false)
                .performanceOrder(dto.performanceOrder())
                .build();

        EventArtist savedEventArtist = eventArtistRepository.save(eventArtist);
        return EventArtistResponseDTO.fromEntity(savedEventArtist);
    }

    @Transactional
    public void removeEventArtist(Long eventId, Long eventArtistId, UUID organizerId) {
        Event event = getEventById(eventId);
        verifyOwnership(event, organizerId);

        EventArtist eventArtist = eventArtistRepository.findById(eventArtistId)
                .orElseThrow(() -> new NotFoundException("EventArtist", eventArtistId));

        if (!eventArtist.getEvent().getId().equals(eventId)) {
            throw new BadRequestException("L'artista non appartiene a questo evento");
        }

        eventArtistRepository.delete(eventArtist);
    }

    // === Delete ===

    @Transactional
    public void delete(Long id, UUID organizerId) {
        Event event = getEventById(id);
        verifyOwnership(event, organizerId);

        if (event.getImageUrl() != null) {
            cloudinaryService.deleteImage(event.getImageUrl());
        }

        eventRepository.delete(event);
    }

    // === Helper Methods ===

    private void verifyOwnership(Event event, UUID organizerId) {
        if (!event.getOrganizer().getId().equals(organizerId)) {
            throw new BadRequestException("Non sei l'organizzatore di questo evento");
        }
    }

    // Metodo per ADMIN (non verifica ownership)
    @Transactional
    public void adminDelete(Long id) {
        Event event = getEventById(id);

        if (event.getImageUrl() != null) {
            cloudinaryService.deleteImage(event.getImageUrl());
        }

        eventRepository.delete(event);
    }
}