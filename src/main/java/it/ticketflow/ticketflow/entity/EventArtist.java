package it.ticketflow.ticketflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isHeadliner = false;

    @Column(length = 50)
    private String performanceOrder;

    // Relazione: Molti EventArtist appartengono a un Event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Relazione: Molti EventArtist fanno riferimento a un Artist
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
}