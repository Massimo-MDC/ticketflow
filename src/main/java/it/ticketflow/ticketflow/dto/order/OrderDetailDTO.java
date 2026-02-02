package it.ticketflow.ticketflow.dto.order;

import it.ticketflow.ticketflow.dto.ticket.TicketResponseDTO;
import it.ticketflow.ticketflow.entity.Order;
import it.ticketflow.ticketflow.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailDTO(
        UUID id,
        String orderNumber,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        OrderStatus status,
        String paymentIntentId,
        UUID userId,
        String userFullName,
        String userEmail,
        List<TicketResponseDTO> tickets
) {
    public static OrderDetailDTO fromEntity(Order order) {
        List<TicketResponseDTO> ticketDTOs = order.getTickets().stream()
                .map(TicketResponseDTO::fromEntity)
                .toList();

        return new OrderDetailDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentIntentId(),
                order.getUser().getId(),
                order.getUser().getFullName(),
                order.getUser().getEmail(),
                ticketDTOs
        );
    }
}