package it.ticketflow.ticketflow.repository;

import it.ticketflow.ticketflow.entity.Event;
import it.ticketflow.ticketflow.entity.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Derived Queries
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Page<Event> findByVenueCity(String city, Pageable pageable);

    Page<Event> findByStatusAndCategoryId(EventStatus status, Long categoryId, Pageable pageable);

    Page<Event> findByEventDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Event> findByOrganizerId(UUID organizerId, Pageable pageable);

    // JPQL con FETCH JOIN per evitare N+1
    @Query("SELECT e FROM Event e " +
            "JOIN FETCH e.venue " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.organizer " +
            "WHERE e.id = :id")
    Optional<Event> findByIdWithDetails(@Param("id") Long id);

    // Query per eventi con biglietti disponibili
    @Query("SELECT DISTINCT e FROM Event e " +
            "JOIN e.eventSectors es " +
            "WHERE e.status = :status AND es.availableTickets > 0")
    Page<Event> findAvailableEvents(@Param("status") EventStatus status, Pageable pageable);

    // Ricerca full-text su titolo e descrizione
    @Query("SELECT e FROM Event e WHERE " +
            "LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Event> searchEvents(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Top eventi per biglietti venduti (Native Query)
    @Query(value = """
            SELECT e.* FROM events e
            JOIN event_sectors es ON e.id = es.event_id
            JOIN tickets t ON es.id = t.event_sector_id
            WHERE e.status = 'PUBLISHED'
            GROUP BY e.id
            ORDER BY COUNT(t.id) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Event> findTopEventsByTicketsSold(@Param("limit") int limit);

    // Query aggiuntive per GraphQL QueryController

    // Alias per searchEvents usata in GraphQL
    @Query("SELECT e FROM Event e WHERE " +
            "LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Event> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Query per categoria (senza filtro status)
    Page<Event> findByCategoryId(Long categoryId, Pageable pageable);

    // Query per eventi con biglietti disponibili (alias)
    @Query("SELECT DISTINCT e FROM Event e " +
            "JOIN e.eventSectors es " +
            "WHERE e.status = 'PUBLISHED' AND es.availableTickets > 0")
    Page<Event> findEventsWithAvailableTickets(Pageable pageable);

    // Top eventi per GraphQL (versione con Pageable)
    @Query(value = """
            SELECT e.* FROM events e
            JOIN event_sectors es ON e.id = es.event_id
            LEFT JOIN tickets t ON es.id = t.event_sector_id
            WHERE e.status = 'PUBLISHED'
            GROUP BY e.id
            ORDER BY COUNT(t.id) DESC
            """, nativeQuery = true)
    List<Event> findTopEventsByTicketsSold(Pageable pageable);
}