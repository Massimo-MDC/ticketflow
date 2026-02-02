package it.ticketflow.ticketflow.dto.graphql;

import java.util.List;

public record CreateOrderInput(
        List<OrderItemInput> items
) {}