package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.dto.ticket.TicketDetailDTO;
import it.ticketflow.ticketflow.dto.ticket.TicketResponseDTO;
import it.ticketflow.ticketflow.entity.Ticket;
import it.ticketflow.ticketflow.entity.enums.TicketStatus;
import it.ticketflow.ticketflow.exception.BadRequestException;
import it.ticketflow.ticketflow.exception.NotFoundException;
import it.ticketflow.ticketflow.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Page<TicketResponseDTO> findByUser(UUID userId, Pageable pageable) {
        return ticketRepository.findByOrderUserId(userId, pageable)
                .map(TicketResponseDTO::fromEntity);
    }

    public Page<TicketDetailDTO> findByUserWithDetails(UUID userId, Pageable pageable) {
        return ticketRepository.findByUserIdWithEventDetails(userId, pageable)
                .map(TicketDetailDTO::fromEntity);
    }

    public TicketDetailDTO findById(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket", id));
        return TicketDetailDTO.fromEntity(ticket);
    }

    public TicketDetailDTO findByTicketCode(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new NotFoundException("Ticket con codice " + ticketCode + " non trovato"));
        return TicketDetailDTO.fromEntity(ticket);
    }

    @Transactional
    public TicketDetailDTO validateTicket(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new NotFoundException("Ticket con codice " + ticketCode + " non trovato"));

        if (ticket.getStatus() != TicketStatus.VALID) {
            throw new BadRequestException("Il biglietto non è valido (stato: " + ticket.getStatus() + ")");
        }

        ticket.setStatus(TicketStatus.USED);
        Ticket savedTicket = ticketRepository.save(ticket);

        return TicketDetailDTO.fromEntity(savedTicket);
    }

    public Long countValidTicketsByEvent(Long eventId) {
        return ticketRepository.countValidTicketsByEventId(eventId);
    }
}