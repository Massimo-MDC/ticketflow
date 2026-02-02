package it.ticketflow.ticketflow.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "sport_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SportEvent extends Event {

    @Column(length = 100)
    private String homeTeam;

    @Column(length = 100)
    private String awayTeam;

    @Column(length = 50)
    private String league;

    @Override
    public String getEventType() {
        return "SPORT";
    }
}