package it.ticketflow.ticketflow.controller;

import it.ticketflow.ticketflow.dto.sector.SectorRequestDTO;
import it.ticketflow.ticketflow.dto.sector.SectorResponseDTO;
import it.ticketflow.ticketflow.dto.venue.VenueRequestDTO;
import it.ticketflow.ticketflow.dto.venue.VenueResponseDTO;
import it.ticketflow.ticketflow.dto.venue.VenueWithSectorsDTO;
import it.ticketflow.ticketflow.service.SectorService;
import it.ticketflow.ticketflow.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;
    private final SectorService sectorService;

    // === Endpoint Pubblici ===

    @GetMapping
    public ResponseEntity<Page<VenueResponseDTO>> getAllVenues(Pageable pageable) {
        Page<VenueResponseDTO> venues = venueService.findAll(pageable);
        return ResponseEntity.ok(venues);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<Page<VenueResponseDTO>> getVenuesByCity(
            @PathVariable String city,
            Pageable pageable) {
        Page<VenueResponseDTO> venues = venueService.findByCity(city, pageable);
        return ResponseEntity.ok(venues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDTO> getVenueById(@PathVariable Long id) {
        VenueResponseDTO venue = venueService.findById(id);
        return ResponseEntity.ok(venue);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<VenueWithSectorsDTO> getVenueWithSectors(@PathVariable Long id) {
        VenueWithSectorsDTO venue = venueService.findByIdWithSectors(id);
        return ResponseEntity.ok(venue);
    }

    @GetMapping("/{venueId}/sectors")
    public ResponseEntity<List<SectorResponseDTO>> getVenueSectors(@PathVariable Long venueId) {
        List<SectorResponseDTO> sectors = sectorService.findByVenueId(venueId);
        return ResponseEntity.ok(sectors);
    }

    // === Endpoint ADMIN ===

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponseDTO> createVenue(@Valid @RequestBody VenueRequestDTO dto) {
        VenueResponseDTO venue = venueService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(venue);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponseDTO> updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueRequestDTO dto) {
        VenueResponseDTO venue = venueService.update(id, dto);
        return ResponseEntity.ok(venue);
    }

    @PatchMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponseDTO> uploadVenueImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        VenueResponseDTO venue = venueService.uploadImage(id, file);
        return ResponseEntity.ok(venue);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // === Sector Management (ADMIN) ===

    @PostMapping("/{venueId}/sectors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectorResponseDTO> createSector(
            @PathVariable Long venueId,
            @Valid @RequestBody SectorRequestDTO dto) {
        // Assicura che il venueId nel path corrisponda a quello nel DTO
        SectorRequestDTO sectorDto = new SectorRequestDTO(
                dto.name(),
                dto.capacity(),
                dto.basePrice(),
                dto.description(),
                venueId
        );
        SectorResponseDTO sector = sectorService.create(sectorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(sector);
    }

    @PutMapping("/{venueId}/sectors/{sectorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectorResponseDTO> updateSector(
            @PathVariable Long venueId,
            @PathVariable Long sectorId,
            @Valid @RequestBody SectorRequestDTO dto) {
        SectorRequestDTO sectorDto = new SectorRequestDTO(
                dto.name(),
                dto.capacity(),
                dto.basePrice(),
                dto.description(),
                venueId
        );
        SectorResponseDTO sector = sectorService.update(sectorId, sectorDto);
        return ResponseEntity.ok(sector);
    }

    @DeleteMapping("/{venueId}/sectors/{sectorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSector(
            @PathVariable Long venueId,
            @PathVariable Long sectorId) {
        sectorService.delete(sectorId);
        return ResponseEntity.noContent().build();
    }
}