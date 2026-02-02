package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.dto.artist.ArtistRequestDTO;
import it.ticketflow.ticketflow.dto.artist.ArtistResponseDTO;
import it.ticketflow.ticketflow.entity.Artist;
import it.ticketflow.ticketflow.exception.NotFoundException;
import it.ticketflow.ticketflow.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final CloudinaryService cloudinaryService;

    public Page<ArtistResponseDTO> findAll(Pageable pageable) {
        return artistRepository.findAll(pageable)
                .map(ArtistResponseDTO::fromEntity);
    }

    public Page<ArtistResponseDTO> findByGenre(String genre, Pageable pageable) {
        return artistRepository.findByGenre(genre, pageable)
                .map(ArtistResponseDTO::fromEntity);
    }

    public Page<ArtistResponseDTO> search(String name, Pageable pageable) {
        return artistRepository.searchByName(name, pageable)
                .map(ArtistResponseDTO::fromEntity);
    }

    public ArtistResponseDTO findById(Long id) {
        Artist artist = getArtistById(id);
        return ArtistResponseDTO.fromEntity(artist);
    }

    public Artist getArtistById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist", id));
    }

    @Transactional
    public ArtistResponseDTO create(ArtistRequestDTO dto) {
        Artist artist = Artist.builder()
                .stageName(dto.stageName())
                .bio(dto.bio())
                .genre(dto.genre())
                .build();

        Artist savedArtist = artistRepository.save(artist);
        return ArtistResponseDTO.fromEntity(savedArtist);
    }

    @Transactional
    public ArtistResponseDTO update(Long id, ArtistRequestDTO dto) {
        Artist artist = getArtistById(id);

        artist.setStageName(dto.stageName());
        artist.setBio(dto.bio());
        artist.setGenre(dto.genre());

        Artist savedArtist = artistRepository.save(artist);
        return ArtistResponseDTO.fromEntity(savedArtist);
    }

    @Transactional
    public ArtistResponseDTO uploadImage(Long id, MultipartFile file) {
        Artist artist = getArtistById(id);

        if (artist.getImageUrl() != null) {
            cloudinaryService.deleteImage(artist.getImageUrl());
        }

        String imageUrl = cloudinaryService.uploadImage(file, "artists");
        artist.setImageUrl(imageUrl);

        Artist savedArtist = artistRepository.save(artist);
        return ArtistResponseDTO.fromEntity(savedArtist);
    }

    @Transactional
    public void delete(Long id) {
        Artist artist = getArtistById(id);

        if (artist.getImageUrl() != null) {
            cloudinaryService.deleteImage(artist.getImageUrl());
        }

        artistRepository.delete(artist);
    }
}