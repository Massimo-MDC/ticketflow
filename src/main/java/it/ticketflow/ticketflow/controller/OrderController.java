package it.ticketflow.ticketflow.controller;

import it.ticketflow.ticketflow.dto.order.OrderDetailDTO;
import it.ticketflow.ticketflow.dto.order.OrderRequestDTO;
import it.ticketflow.ticketflow.dto.order.OrderResponseDTO;
import it.ticketflow.ticketflow.entity.User;
import it.ticketflow.ticketflow.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // === Endpoint USER ===

    @GetMapping("/my-orders")
    public ResponseEntity<Page<OrderResponseDTO>> getMyOrders(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<OrderResponseDTO> orders = orderService.findByUser(user.getId(), pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my-orders/{id}")
    public ResponseEntity<OrderDetailDTO> getMyOrderById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        OrderDetailDTO order = orderService.findById(id);

        // Verifica che l'ordine appartenga all'utente
        if (!order.userId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderDetailDTO> createOrder(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody OrderRequestDTO dto) {
        OrderDetailDTO order = orderService.createOrder(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderDetailDTO> confirmOrder(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam String paymentIntentId) {
        // In un'app reale, qui verificheresti il payment con Stripe
        OrderDetailDTO order = orderService.confirmOrder(id, paymentIntentId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDetailDTO> cancelOrder(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        OrderDetailDTO order = orderService.cancelOrder(id, user.getId());
        return ResponseEntity.ok(order);
    }

    // === Endpoint ADMIN ===

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(Pageable pageable) {
        Page<OrderResponseDTO> orders = orderService.findAll(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDetailDTO> getOrderById(@PathVariable UUID id) {
        OrderDetailDTO order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDetailDTO> getOrderByNumber(@PathVariable String orderNumber) {
        OrderDetailDTO order = orderService.findByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByUser(
            @PathVariable UUID userId,
            Pageable pageable) {
        Page<OrderResponseDTO> orders = orderService.findByUser(userId, pageable);
        return ResponseEntity.ok(orders);
    }
}