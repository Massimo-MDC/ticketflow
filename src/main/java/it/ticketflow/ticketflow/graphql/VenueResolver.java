package it.ticketflow.ticketflow.graphql;

import it.ticketflow.ticketflow.entity.Sector;
import it.ticketflow.ticketflow.entity.Venue;
import it.ticketflow.ticketflow.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VenueResolver {

    private final SectorRepository sectorRepository;

    @SchemaMapping(typeName = "Venue", field = "sectors")
    public List<Sector> getSectors(Venue venue) {
        return sectorRepository.findByVenueId(venue.getId());
    }
}