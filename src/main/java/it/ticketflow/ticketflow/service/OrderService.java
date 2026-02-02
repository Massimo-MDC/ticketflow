package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.dto.order.*;
import it.ticketflow.ticketflow.entity.*;
import it.ticketflow.ticketflow.entity.enums.OrderStatus;
import it.ticketflow.ticketflow.entity.enums.TicketStatus;
import it.ticketflow.ticketflow.exception.BadRequestException;
import it.ticketflow.ticketflow.exception.NotFoundException;
import it.ticketflow.ticketflow.repository.EventSectorRepository;
import it.ticketflow.ticketflow.repository.OrderRepository;
import it.ticketflow.ticketflow.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final EventSectorRepository eventSectorRepository;
    private final UserService userService;
    private final EmailService emailService;

    public Page<OrderResponseDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(OrderResponseDTO::fromEntity);
    }

    public Page<OrderResponseDTO> findByUser(UUID userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderResponseDTO::fromEntity);
    }

    public OrderDetailDTO findById(UUID id) {
        Order order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Order", id));
        return OrderDetailDTO.fromEntity(order);
    }

    public OrderDetailDTO findByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException("Order con numero " + orderNumber + " non trovato"));
        return OrderDetailDTO.fromEntity(order);
    }

    @Transactional
    public OrderDetailDTO createOrder(OrderRequestDTO dto, UUID userId) {
        User user = userService.getUserById(userId);

        // Verifica disponibilità e calcola totale
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<EventSector> eventSectorsToUpdate = new ArrayList<>();
        List<TicketCreationInfo> ticketsToCreate = new ArrayList<>();

        for (OrderItemDTO item : dto.items()) {
            EventSector eventSector = eventSectorRepository.findById(item.eventSectorId())
                    .orElseThrow(() -> new NotFoundException("EventSector", item.eventSectorId()));

            // Verifica disponibilità
            if (eventSector.getAvailableTickets() < item.quantity()) {
                throw new BadRequestException(
                        "Non ci sono abbastanza biglietti disponibili per il settore " +
                                eventSector.getSector().getName() +
                                ". Disponibili: " + eventSector.getAvailableTickets() +
                                ", Richiesti: " + item.quantity()
                );
            }

            // Calcola prezzo
            BigDecimal itemTotal = eventSector.getCurrentPrice()
                    .multiply(BigDecimal.valueOf(item.quantity()));
            totalAmount = totalAmount.add(itemTotal);

            // Prepara l'aggiornamento
            eventSector.setAvailableTickets(eventSector.getAvailableTickets() - item.quantity());
            eventSectorsToUpdate.add(eventSector);

            // Prepara i biglietti
            for (int i = 0; i < item.quantity(); i++) {
                ticketsToCreate.add(new TicketCreationInfo(eventSector, eventSector.getCurrentPrice()));
            }
        }

        // Crea l'ordine
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Aggiorna i settori
        eventSectorRepository.saveAll(eventSectorsToUpdate);

        // Crea i biglietti
        List<Ticket> tickets = ticketsToCreate.stream()
                .map(info -> Ticket.builder()
                        .order(savedOrder)
                        .eventSector(info.eventSector())
                        .purchasePrice(info.price())
                        .status(TicketStatus.VALID)
                        .build())
                .toList();

        ticketRepository.saveAll(tickets);

        // Ricarica l'ordine con i biglietti
        Order completeOrder = orderRepository.findByIdWithDetails(savedOrder.getId())
                .orElseThrow();

        return OrderDetailDTO.fromEntity(completeOrder);
    }

    @Transactional
    public OrderDetailDTO confirmOrder(UUID orderId, String paymentIntentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order", orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("L'ordine non può essere confermato (stato attuale: " + order.getStatus() + ")");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentIntentId(paymentIntentId);

        Order savedOrder = orderRepository.save(order);

        // Invia email di conferma
        emailService.sendOrderConfirmation(savedOrder);

        return OrderDetailDTO.fromEntity(savedOrder);
    }

    @Transactional
    public OrderDetailDTO cancelOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new NotFoundException("Order", orderId));

        // Verifica ownership
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Non puoi cancellare un ordine che non ti appartiene");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("L'ordine non può essere cancellato (stato attuale: " + order.getStatus() + ")");
        }

        // Ripristina disponibilità biglietti
        for (Ticket ticket : order.getTickets()) {
            EventSector eventSector = ticket.getEventSector();
            eventSector.incrementAvailableTickets();
            eventSectorRepository.save(eventSector);

            ticket.setStatus(TicketStatus.CANCELLED);
            ticketRepository.save(ticket);
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return OrderDetailDTO.fromEntity(savedOrder);
    }

    // Record helper per la creazione dei biglietti
    private record TicketCreationInfo(EventSector eventSector, BigDecimal price) {}
}