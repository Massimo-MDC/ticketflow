package it.ticketflow.ticketflow.repository;

import it.ticketflow.ticketflow.entity.Ticket;
import it.ticketflow.ticketflow.entity.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    // Order.id è UUID
    List<Ticket> findByOrderId(UUID orderId);

    Optional<Ticket> findByTicketCode(String ticketCode);

    Page<Ticket> findByOrderUserId(UUID userId, Pageable pageable);

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    // Alias per QueryController GraphQL
    @Query("SELECT t FROM Ticket t WHERE t.order.user.id = :userId")
    Page<Ticket> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT t FROM Ticket t " +
            "JOIN FETCH t.eventSector es " +
            "JOIN FETCH es.event e " +
            "JOIN FETCH es.sector " +
            "WHERE t.order.user.id = :userId")
    Page<Ticket> findByUserIdWithEventDetails(@Param("userId") UUID userId, Pageable pageable);

    // Conteggio biglietti venduti per evento
    @Query("SELECT COUNT(t) FROM Ticket t " +
            "WHERE t.eventSector.event.id = :eventId AND t.status = 'VALID'")
    Long countValidTicketsByEventId(@Param("eventId") Long eventId);
}