package it.ticketflow.ticketflow.graphql;

import it.ticketflow.ticketflow.dto.graphql.CreateOrderInput;
import it.ticketflow.ticketflow.entity.*;
import it.ticketflow.ticketflow.entity.enums.OrderStatus;
import it.ticketflow.ticketflow.entity.enums.TicketStatus;
import it.ticketflow.ticketflow.exception.BadRequestException;
import it.ticketflow.ticketflow.exception.NotFoundException;
import it.ticketflow.ticketflow.repository.*;
import it.ticketflow.ticketflow.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MutationController {

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final EventSectorRepository eventSectorRepository;
    private final EmailService emailService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Order createOrder(@AuthenticationPrincipal User user, @Argument CreateOrderInput input) {
        // Calculate total and validate availability
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<EventSector> eventSectors = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        for (var item : input.items()) {
            EventSector eventSector = eventSectorRepository.findById(item.eventSectorId())
                    .orElseThrow(() -> new NotFoundException("EventSector", item.eventSectorId()));

            if (eventSector.getAvailableTickets() < item.quantity()) {
                throw new BadRequestException("Not enough tickets available for sector: " + eventSector.getSector().getName());
            }

            totalAmount = totalAmount.add(eventSector.getCurrentPrice().multiply(BigDecimal.valueOf(item.quantity())));
            eventSectors.add(eventSector);
            quantities.add(item.quantity());
        }

        // Create order - orderNumber e orderDate sono gestiti da @PrePersist e @CreatedDate
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();
        order = orderRepository.save(order);

        // Create tickets and update availability
        for (int i = 0; i < eventSectors.size(); i++) {
            EventSector eventSector = eventSectors.get(i);
            int quantity = quantities.get(i);

            // Decrement available tickets
            eventSector.setAvailableTickets(eventSector.getAvailableTickets() - quantity);
            eventSectorRepository.save(eventSector);

            // Create tickets - ticketCode gestito da @PrePersist
            for (int j = 0; j < quantity; j++) {
                Ticket ticket = Ticket.builder()
                        .order(order)
                        .eventSector(eventSector)
                        .purchasePrice(eventSector.getCurrentPrice())
                        .status(TicketStatus.VALID)
                        .build();
                ticketRepository.save(ticket);
            }
        }

        return orderRepository.findByIdWithDetails(order.getId()).orElse(order);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Order confirmOrder(@AuthenticationPrincipal User user,
                              @Argument UUID orderId,
                              @Argument String paymentIntentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order", orderId));

        // Verify ownership
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only confirm your own orders");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is not in PENDING status");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentIntentId(paymentIntentId);
        Order savedOrder = orderRepository.save(order);

        // Send confirmation email
        emailService.sendOrderConfirmation(savedOrder);

        return savedOrder;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public Order cancelOrder(@AuthenticationPrincipal User user, @Argument UUID orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new NotFoundException("Order", orderId));

        // Verify ownership
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only cancel your own orders");
        }

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED) {
            throw new BadRequestException("Order is already cancelled or refunded");
        }

        // Restore ticket availability
        List<Ticket> tickets = ticketRepository.findByOrderId(order.getId());
        for (Ticket ticket : tickets) {
            EventSector eventSector = ticket.getEventSector();
            eventSector.setAvailableTickets(eventSector.getAvailableTickets() + 1);
            eventSectorRepository.save(eventSector);

            ticket.setStatus(TicketStatus.CANCELLED);
            ticketRepository.save(ticket);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @Transactional
    public Ticket validateTicket(@Argument String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new NotFoundException("Ticket with code", ticketCode));

        if (ticket.getStatus() != TicketStatus.VALID) {
            throw new BadRequestException("Ticket is not valid. Current status: " + ticket.getStatus());
        }

        ticket.setStatus(TicketStatus.USED);
        return ticketRepository.save(ticket);
    }
}
