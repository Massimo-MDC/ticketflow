package it.ticketflow.ticketflow.dto.graphql;


import it.ticketflow.ticketflow.entity.enums.EventStatus;

import java.time.LocalDate;

public record EventFilterInput(
        String city,
        Long categoryId,
        EventStatus status,
        LocalDate startDate,
        LocalDate endDate,
        String searchTerm
) {}