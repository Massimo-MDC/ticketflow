package it.ticketflow.ticketflow.repository;

import it.ticketflow.ticketflow.entity.EventSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSectorRepository extends JpaRepository<EventSector, Long> {

    List<EventSector> findByEventId(Long eventId);

    @Query("SELECT es FROM EventSector es " +
            "JOIN FETCH es.sector " +
            "WHERE es.event.id = :eventId AND es.availableTickets > 0")
    List<EventSector> findAvailableSectorsByEventId(@Param("eventId") Long eventId);
}