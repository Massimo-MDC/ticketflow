package it.ticketflow.ticketflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String region;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(length = 10)
    private String zipCode;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private Integer totalCapacity;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    // Relazione: Una Venue ha molti Sector
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Sector> sectors = new ArrayList<>();

    // Relazione: Una Venue ospita molti Event
    @OneToMany(mappedBy = "venue", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    // === Metodi Helper per relazione bidirezionale ===

    public void addSector(Sector sector) {
        sectors.add(sector);
        sector.setVenue(this);
    }

    public void removeSector(Sector sector) {
        sectors.remove(sector);
        sector.setVenue(null);
    }
}