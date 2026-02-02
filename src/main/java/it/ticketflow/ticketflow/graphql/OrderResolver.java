package it.ticketflow.ticketflow.graphql;

import it.ticketflow.ticketflow.entity.Order;
import it.ticketflow.ticketflow.entity.Ticket;
import it.ticketflow.ticketflow.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderResolver {

    private final TicketRepository ticketRepository;

    @SchemaMapping(typeName = "Order", field = "tickets")
    public List<Ticket> getTickets(Order order) {
        return ticketRepository.findByOrderId(order.getId());
    }
}