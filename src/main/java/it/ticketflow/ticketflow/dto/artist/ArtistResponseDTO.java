package it.ticketflow.ticketflow.dto.artist;

import it.ticketflow.ticketflow.entity.Artist;

public record ArtistResponseDTO(
        Long id,
        String stageName,
        String bio,
        String imageUrl,
        String genre
) {
    public static ArtistResponseDTO fromEntity(Artist artist) {
        return new ArtistResponseDTO(
                artist.getId(),
                artist.getStageName(),
                artist.getBio(),
                artist.getImageUrl(),
                artist.getGenre()
        );
    }
}