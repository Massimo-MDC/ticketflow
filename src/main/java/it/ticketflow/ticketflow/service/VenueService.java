package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.dto.venue.VenueRequestDTO;
import it.ticketflow.ticketflow.dto.venue.VenueResponseDTO;
import it.ticketflow.ticketflow.dto.venue.VenueWithSectorsDTO;
import it.ticketflow.ticketflow.entity.Venue;
import it.ticketflow.ticketflow.exception.NotFoundException;
import it.ticketflow.ticketflow.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final CloudinaryService cloudinaryService;

    public Page<VenueResponseDTO> findAll(Pageable pageable) {
        return venueRepository.findAll(pageable)
                .map(VenueResponseDTO::fromEntity);
    }

    public Page<VenueResponseDTO> findByCity(String city, Pageable pageable) {
        return venueRepository.findByCity(city, pageable)
                .map(VenueResponseDTO::fromEntity);
    }

    public VenueResponseDTO findById(Long id) {
        Venue venue = getVenueById(id);
        return VenueResponseDTO.fromEntity(venue);
    }

    public VenueWithSectorsDTO findByIdWithSectors(Long id) {
        Venue venue = venueRepository.findByIdWithSectors(id)
                .orElseThrow(() -> new NotFoundException("Venue", id));
        return VenueWithSectorsDTO.fromEntity(venue);
    }

    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue", id));
    }

    @Transactional
    public VenueResponseDTO create(VenueRequestDTO dto) {
        Venue venue = Venue.builder()
                .name(dto.name())
                .address(dto.address())
                .city(dto.city())
                .region(dto.region())
                .country(dto.country())
                .zipCode(dto.zipCode())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .totalCapacity(dto.totalCapacity())
                .description(dto.description())
                .build();

        Venue savedVenue = venueRepository.save(venue);
        return VenueResponseDTO.fromEntity(savedVenue);
    }

    @Transactional
    public VenueResponseDTO update(Long id, VenueRequestDTO dto) {
        Venue venue = getVenueById(id);

        venue.setName(dto.name());
        venue.setAddress(dto.address());
        venue.setCity(dto.city());
        venue.setRegion(dto.region());
        venue.setCountry(dto.country());
        venue.setZipCode(dto.zipCode());
        venue.setLatitude(dto.latitude());
        venue.setLongitude(dto.longitude());
        venue.setTotalCapacity(dto.totalCapacity());
        venue.setDescription(dto.description());

        Venue savedVenue = venueRepository.save(venue);
        return VenueResponseDTO.fromEntity(savedVenue);
    }

    @Transactional
    public VenueResponseDTO uploadImage(Long id, MultipartFile file) {
        Venue venue = getVenueById(id);

        if (venue.getImageUrl() != null) {
            cloudinaryService.deleteImage(venue.getImageUrl());
        }

        String imageUrl = cloudinaryService.uploadImage(file, "venues");
        venue.setImageUrl(imageUrl);

        Venue savedVenue = venueRepository.save(venue);
        return VenueResponseDTO.fromEntity(savedVenue);
    }

    @Transactional
    public void delete(Long id) {
        Venue venue = getVenueById(id);

        if (venue.getImageUrl() != null) {
            cloudinaryService.deleteImage(venue.getImageUrl());
        }

        venueRepository.delete(venue);
    }
}