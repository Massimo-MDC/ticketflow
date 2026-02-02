package it.ticketflow.ticketflow.repository;

import it.ticketflow.ticketflow.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {

    List<Sector> findByVenueId(Long venueId);
}