package it.ticketflow.ticketflow.dto.order;

import it.ticketflow.ticketflow.entity.Order;
import it.ticketflow.ticketflow.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        String orderNumber,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        OrderStatus status,
        UUID userId,
        String userFullName,
        int ticketCount
) {
    public static OrderResponseDTO fromEntity(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getUser().getId(),
                order.getUser().getFullName(),
                order.getTickets().size()
        );
    }
}