package it.ticketflow.ticketflow.entity;

import it.ticketflow.ticketflow.entity.enums.EventStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private LocalTime eventTime;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventStatus status = EventStatus.DRAFT;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Relazione: Molti Event appartengono a una Venue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    // Relazione: Molti Event sono creati da un User (ORGANIZER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    // Relazione: Molti Event appartengono a una Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Relazione: Un Event ha molti EventSector (settori disponibili per questo evento)
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventSector> eventSectors = new ArrayList<>();

    // Relazione: Un Event ha molti EventArtist
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventArtist> eventArtists = new ArrayList<>();

    // === Metodi Helper ===

    public void addEventSector(EventSector eventSector) {
        eventSectors.add(eventSector);
        eventSector.setEvent(this);
    }

    public void removeEventSector(EventSector eventSector) {
        eventSectors.remove(eventSector);
        eventSector.setEvent(null);
    }

    public void addEventArtist(EventArtist eventArtist) {
        eventArtists.add(eventArtist);
        eventArtist.setEvent(this);
    }

    public void removeEventArtist(EventArtist eventArtist) {
        eventArtists.remove(eventArtist);
        eventArtist.setEvent(null);
    }

    // Metodo astratto opzionale per polimorfismo
    public abstract String getEventType();
}