package it.ticketflow.ticketflow.dto.graphql;

public record OrderItemInput(
        Long eventSectorId,
        Integer quantity
) {}