package it.ticketflow.ticketflow.controller;

import it.ticketflow.ticketflow.dto.ticket.TicketDetailDTO;
import it.ticketflow.ticketflow.dto.ticket.TicketResponseDTO;
import it.ticketflow.ticketflow.entity.User;
import it.ticketflow.ticketflow.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // === Endpoint USER ===

    @GetMapping("/my-tickets")
    public ResponseEntity<Page<TicketResponseDTO>> getMyTickets(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<TicketResponseDTO> tickets = ticketService.findByUser(user.getId(), pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/my-tickets/details")
    public ResponseEntity<Page<TicketDetailDTO>> getMyTicketsWithDetails(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<TicketDetailDTO> tickets = ticketService.findByUserWithDetails(user.getId(), pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/my-tickets/{id}")
    public ResponseEntity<TicketDetailDTO> getMyTicketById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        TicketDetailDTO ticket = ticketService.findById(id);
        // Il controllo di ownership viene fatto nella query del repository
        return ResponseEntity.ok(ticket);
    }

    // === Endpoint ORGANIZER (validazione biglietti) ===

    @GetMapping("/validate/{ticketCode}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<TicketDetailDTO> getTicketByCode(@PathVariable String ticketCode) {
        TicketDetailDTO ticket = ticketService.findByTicketCode(ticketCode);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/validate/{ticketCode}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<TicketDetailDTO> validateTicket(@PathVariable String ticketCode) {
        TicketDetailDTO ticket = ticketService.validateTicket(ticketCode);
        return ResponseEntity.ok(ticket);
    }

    // === Endpoint ADMIN ===

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDetailDTO> getTicketById(@PathVariable UUID id) {
        TicketDetailDTO ticket = ticketService.findById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/event/{eventId}/count")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Long> countTicketsByEvent(@PathVariable Long eventId) {
        Long count = ticketService.countValidTicketsByEvent(eventId);
        return ResponseEntity.ok(count);
    }
}