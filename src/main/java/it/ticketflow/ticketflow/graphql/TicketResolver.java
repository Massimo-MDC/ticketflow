package it.ticketflow.ticketflow.graphql;

import it.ticketflow.ticketflow.entity.Event;
import it.ticketflow.ticketflow.entity.Ticket;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TicketResolver {

    @SchemaMapping(typeName = "Ticket", field = "event")
    public Event getEvent(Ticket ticket) {
        return ticket.getEventSector().getEvent();
    }
}