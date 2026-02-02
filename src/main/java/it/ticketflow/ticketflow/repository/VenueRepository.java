package it.ticketflow.ticketflow.repository;

import it.ticketflow.ticketflow.entity.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    Page<Venue> findByCity(String city, Pageable pageable);

    Page<Venue> findByCityAndCountry(String city, String country, Pageable pageable);

    @Query("SELECT v FROM Venue v WHERE v.totalCapacity >= :minCapacity")
    List<Venue> findByMinCapacity(@Param("minCapacity") Integer minCapacity);

    @Query("SELECT v FROM Venue v LEFT JOIN FETCH v.sectors WHERE v.id = :id")
    Optional<Venue> findByIdWithSectors(@Param("id") Long id);
}