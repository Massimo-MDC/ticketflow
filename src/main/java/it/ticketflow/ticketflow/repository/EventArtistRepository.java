package it.ticketflow.ticketflow.repository;

import it.ticketflow.ticketflow.entity.EventArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventArtistRepository extends JpaRepository<EventArtist, Long> {

    List<EventArtist> findByEventId(Long eventId);

    @Query("SELECT ea FROM EventArtist ea " +
            "JOIN FETCH ea.artist " +
            "WHERE ea.event.id = :eventId " +
            "ORDER BY ea.isHeadliner DESC")
    List<EventArtist> findByEventIdWithArtists(@Param("eventId") Long eventId);

    List<EventArtist> findByArtistId(Long artistId);
}