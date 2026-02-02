package it.ticketflow.ticketflow.repository;

import it.ticketflow.ticketflow.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Page<Artist> findByGenre(String genre, Pageable pageable);

    @Query("SELECT a FROM Artist a WHERE LOWER(a.stageName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Artist> searchByName(@Param("name") String name, Pageable pageable);
}