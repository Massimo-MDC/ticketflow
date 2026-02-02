package it.ticketflow.ticketflow.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "theater_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TheaterEvent extends Event {

    @Column(length = 100)
    private String director;

    private Integer durationMinutes;

    @Column(length = 20)
    private String ageRestriction;

    @Override
    public String getEventType() {
        return "THEATER";
    }
}