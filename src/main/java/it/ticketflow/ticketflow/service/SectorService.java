package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.dto.sector.SectorRequestDTO;
import it.ticketflow.ticketflow.dto.sector.SectorResponseDTO;
import it.ticketflow.ticketflow.entity.Sector;
import it.ticketflow.ticketflow.entity.Venue;
import it.ticketflow.ticketflow.exception.NotFoundException;
import it.ticketflow.ticketflow.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;
    private final VenueService venueService;

    public List<SectorResponseDTO> findByVenueId(Long venueId) {
        return sectorRepository.findByVenueId(venueId).stream()
                .map(SectorResponseDTO::fromEntity)
                .toList();
    }

    public SectorResponseDTO findById(Long id) {
        Sector sector = getSectorById(id);
        return SectorResponseDTO.fromEntity(sector);
    }

    public Sector getSectorById(Long id) {
        return sectorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sector", id));
    }

    @Transactional
    public SectorResponseDTO create(SectorRequestDTO dto) {
        Venue venue = venueService.getVenueById(dto.venueId());

        Sector sector = Sector.builder()
                .name(dto.name())
                .capacity(dto.capacity())
                .basePrice(dto.basePrice())
                .description(dto.description())
                .venue(venue)
                .build();

        Sector savedSector = sectorRepository.save(sector);
        return SectorResponseDTO.fromEntity(savedSector);
    }

    @Transactional
    public SectorResponseDTO update(Long id, SectorRequestDTO dto) {
        Sector sector = getSectorById(id);
        Venue venue = venueService.getVenueById(dto.venueId());

        sector.setName(dto.name());
        sector.setCapacity(dto.capacity());
        sector.setBasePrice(dto.basePrice());
        sector.setDescription(dto.description());
        sector.setVenue(venue);

        Sector savedSector = sectorRepository.save(sector);
        return SectorResponseDTO.fromEntity(savedSector);
    }

    @Transactional
    public void delete(Long id) {
        Sector sector = getSectorById(id);
        sectorRepository.delete(sector);
    }
}