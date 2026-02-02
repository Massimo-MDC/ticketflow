package it.ticketflow.ticketflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String stageName;

    @Column(length = 1000)
    private String bio;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 50)
    private String genre;

    // Relazione: Un Artist può partecipare a molti Event tramite EventArtist
    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
    @Builder.Default
    private List<EventArtist> eventArtists = new ArrayList<>();
}