package it.ticketflow.ticketflow.controller;

import it.ticketflow.ticketflow.dto.event.*;
import it.ticketflow.ticketflow.entity.User;
import it.ticketflow.ticketflow.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // === Endpoint Pubblici ===

    @GetMapping("/public")
    public ResponseEntity<Page<EventResponseDTO>> getPublishedEvents(Pageable pageable) {
        Page<EventResponseDTO> events = eventService.findPublished(pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/public/city/{city}")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCity(
            @PathVariable String city,
            Pageable pageable) {
        Page<EventResponseDTO> events = eventService.findByCity(city, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        Page<EventResponseDTO> events = eventService.findByCategory(categoryId, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/public/date-range")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<EventResponseDTO> events = eventService.findByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/public/available")
    public ResponseEntity<Page<EventResponseDTO>> getAvailableEvents(Pageable pageable) {
        Page<EventResponseDTO> events = eventService.findAvailable(pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/public/search")
    public ResponseEntity<Page<EventResponseDTO>> searchEvents(
            @RequestParam String q,
            Pageable pageable) {
        Page<EventResponseDTO> events = eventService.search(q, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/public/top")
    public ResponseEntity<List<EventResponseDTO>> getTopEvents(
            @RequestParam(defaultValue = "10") int limit) {
        List<EventResponseDTO> events = eventService.findTopEvents(limit);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<EventDetailDTO> getEventById(@PathVariable Long id) {
        EventDetailDTO event = eventService.findById(id);
        return ResponseEntity.ok(event);
    }

    // === Endpoint ORGANIZER ===

    @GetMapping("/my-events")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Page<EventResponseDTO>> getMyEvents(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<EventResponseDTO> events = eventService.findByOrganizer(user.getId(), pageable);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/concert")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponseDTO> createConcertEvent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ConcertEventRequestDTO dto) {
        EventResponseDTO event = eventService.createConcertEvent(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PostMapping("/sport")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponseDTO> createSportEvent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SportEventRequestDTO dto) {
        EventResponseDTO event = eventService.createSportEvent(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PostMapping("/theater")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponseDTO> createTheaterEvent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TheaterEventRequestDTO dto) {
        EventResponseDTO event = eventService.createTheaterEvent(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponseDTO> updateEventStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody EventStatusUpdateDTO dto) {
        EventResponseDTO event = eventService.updateStatus(id, dto.status(), user.getId());
        return ResponseEntity.ok(event);
    }

    @PatchMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponseDTO> uploadEventImage(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        EventResponseDTO event = eventService.uploadImage(id, file, user.getId());
        return ResponseEntity.ok(event);
    }

    // === Event Sectors ===

    @PostMapping("/{eventId}/sectors")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventSectorResponseDTO> addEventSector(
            @AuthenticationPrincipal User user,
            @PathVariable Long eventId,
            @Valid @RequestBody EventSectorRequestDTO dto) {
        EventSectorResponseDTO eventSector = eventService.addEventSector(eventId, dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventSector);
    }

    @DeleteMapping("/{eventId}/sectors/{eventSectorId}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Void> removeEventSector(
            @AuthenticationPrincipal User user,
            @PathVariable Long eventId,
            @PathVariable Long eventSectorId) {
        eventService.removeEventSector(eventId, eventSectorId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // === Event Artists ===

    @PostMapping("/{eventId}/artists")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventArtistResponseDTO> addEventArtist(
            @AuthenticationPrincipal User user,
            @PathVariable Long eventId,
            @Valid @RequestBody EventArtistRequestDTO dto) {
        EventArtistResponseDTO eventArtist = eventService.addEventArtist(eventId, dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventArtist);
    }

    @DeleteMapping("/{eventId}/artists/{eventArtistId}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Void> removeEventArtist(
            @AuthenticationPrincipal User user,
            @PathVariable Long eventId,
            @PathVariable Long eventArtistId) {
        eventService.removeEventArtist(eventId, eventArtistId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // === Delete ===

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Void> deleteEvent(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        eventService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // === ADMIN Only ===

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(Pageable pageable) {
        Page<EventResponseDTO> events = eventService.findAll(pageable);
        return ResponseEntity.ok(events);
    }

    @DeleteMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDeleteEvent(@PathVariable Long id) {
        eventService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }
}