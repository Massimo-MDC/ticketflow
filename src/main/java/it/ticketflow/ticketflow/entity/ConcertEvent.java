package it.ticketflow.ticketflow.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "concert_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConcertEvent extends Event {

    @Column(length = 50)
    private String genre;

    @Column(length = 100)
    private String tourName;

    @Override
    public String getEventType() {
        return "CONCERT";
    }
}