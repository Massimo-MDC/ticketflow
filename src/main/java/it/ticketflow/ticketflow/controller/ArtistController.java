package it.ticketflow.ticketflow.controller;

import it.ticketflow.ticketflow.dto.artist.ArtistRequestDTO;
import it.ticketflow.ticketflow.dto.artist.ArtistResponseDTO;
import it.ticketflow.ticketflow.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    // === Endpoint Pubblici ===

    @GetMapping
    public ResponseEntity<Page<ArtistResponseDTO>> getAllArtists(Pageable pageable) {
        Page<ArtistResponseDTO> artists = artistService.findAll(pageable);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<ArtistResponseDTO>> getArtistsByGenre(
            @PathVariable String genre,
            Pageable pageable) {
        Page<ArtistResponseDTO> artists = artistService.findByGenre(genre, pageable);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArtistResponseDTO>> searchArtists(
            @RequestParam String name,
            Pageable pageable) {
        Page<ArtistResponseDTO> artists = artistService.search(name, pageable);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponseDTO> getArtistById(@PathVariable Long id) {
        ArtistResponseDTO artist = artistService.findById(id);
        return ResponseEntity.ok(artist);
    }

    // === Endpoint ADMIN/ORGANIZER ===

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public ResponseEntity<ArtistResponseDTO> createArtist(@Valid @RequestBody ArtistRequestDTO dto) {
        ArtistResponseDTO artist = artistService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(artist);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public ResponseEntity<ArtistResponseDTO> updateArtist(
            @PathVariable Long id,
            @Valid @RequestBody ArtistRequestDTO dto) {
        ArtistResponseDTO artist = artistService.update(id, dto);
        return ResponseEntity.ok(artist);
    }

    @PatchMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public ResponseEntity<ArtistResponseDTO> uploadArtistImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        ArtistResponseDTO artist = artistService.uploadImage(id, file);
        return ResponseEntity.ok(artist);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}