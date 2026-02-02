package it.ticketflow.ticketflow.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequestDTO(
        @NotEmpty(message = "L'ordine deve contenere almeno un biglietto")
        @Valid
        List<OrderItemDTO> items
) {}