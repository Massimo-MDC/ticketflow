package it.ticketflow.ticketflow.dto.event;

import it.ticketflow.ticketflow.entity.EventArtist;

public record EventArtistResponseDTO(
        Long id,
        Long artistId,
        String artistName,
        String artistImageUrl,
        Boolean isHeadliner,
        String performanceOrder
) {
    public static EventArtistResponseDTO fromEntity(EventArtist eventArtist) {
        return new EventArtistResponseDTO(
                eventArtist.getId(),
                eventArtist.getArtist().getId(),
                eventArtist.getArtist().getStageName(),
                eventArtist.getArtist().getImageUrl(),
                eventArtist.getIsHeadliner(),
                eventArtist.getPerformanceOrder()
        );
    }
}