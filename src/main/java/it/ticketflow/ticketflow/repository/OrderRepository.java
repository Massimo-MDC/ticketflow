package it.ticketflow.ticketflow.repository;

import it.ticketflow.ticketflow.entity.Order;
import it.ticketflow.ticketflow.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findByUserId(UUID userId, Pageable pageable);

    Page<Order> findByUserIdAndStatus(UUID userId, OrderStatus status, Pageable pageable);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.tickets t " +
            "JOIN FETCH t.eventSector es " +
            "JOIN FETCH es.event " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") UUID id);

    // Statistiche: totale ordini per periodo
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    Long countOrdersBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}