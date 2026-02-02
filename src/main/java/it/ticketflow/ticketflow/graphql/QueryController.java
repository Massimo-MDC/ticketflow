package it.ticketflow.ticketflow.graphql;

import it.ticketflow.ticketflow.dto.graphql.*;
import it.ticketflow.ticketflow.entity.*;
import it.ticketflow.ticketflow.entity.enums.EventStatus;
import it.ticketflow.ticketflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class QueryController {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final VenueRepository venueRepository;
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;

    // ==================== USER QUERIES ====================

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public User me(@AuthenticationPrincipal User user) {
        return user;
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User user(@Argument UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserPageDTO users(@Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return UserPageDTO.from(userPage);
    }

    // ==================== CATEGORY QUERIES ====================

    @QueryMapping
    public List<Category> categories() {
        return categoryRepository.findAll();
    }

    @QueryMapping
    public Category category(@Argument Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // ==================== VENUE QUERIES ====================

    @QueryMapping
    public List<Venue> venues(@Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        return venueRepository.findAll(pageable).getContent();
    }

    @QueryMapping
    public Venue venue(@Argument Long id) {
        return venueRepository.findByIdWithSectors(id).orElse(null);
    }

    @QueryMapping
    public List<Venue> venuesByCity(@Argument String city, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        return venueRepository.findByCity(city, pageable).getContent();
    }

    // ==================== ARTIST QUERIES ====================

    @QueryMapping
    public List<Artist> artists(@Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        return artistRepository.findAll(pageable).getContent();
    }

    @QueryMapping
    public Artist artist(@Argument Long id) {
        return artistRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Artist> artistsByGenre(@Argument String genre, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        return artistRepository.findByGenre(genre, pageable).getContent();
    }

    // ==================== EVENT QUERIES ====================

    @QueryMapping
    public EventPageDTO events(@Argument EventFilterInput filter, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage;

        if (filter == null) {
            eventPage = eventRepository.findByStatus(EventStatus.PUBLISHED, pageable);
        } else if (filter.searchTerm() != null && !filter.searchTerm().isBlank()) {
            eventPage = eventRepository.searchByTitleOrDescription(filter.searchTerm(), pageable);
        } else if (filter.city() != null) {
            eventPage = eventRepository.findByVenueCity(filter.city(), pageable);
        } else if (filter.categoryId() != null) {
            eventPage = eventRepository.findByCategoryId(filter.categoryId(), pageable);
        } else if (filter.startDate() != null && filter.endDate() != null) {
            eventPage = eventRepository.findByEventDateBetween(filter.startDate(), filter.endDate(), pageable);
        } else {
            eventPage = eventRepository.findByStatus(EventStatus.PUBLISHED, pageable);
        }

        return EventPageDTO.from(eventPage);
    }

    @QueryMapping
    public Event event(@Argument Long id) {
        return eventRepository.findByIdWithDetails(id).orElse(null);
    }

    @QueryMapping
    public EventPageDTO publishedEvents(@Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findByStatus(EventStatus.PUBLISHED, pageable);
        return EventPageDTO.from(eventPage);
    }

    @QueryMapping
    public EventPageDTO eventsByCity(@Argument String city, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findByVenueCity(city, pageable);
        return EventPageDTO.from(eventPage);
    }

    @QueryMapping
    public EventPageDTO eventsByCategory(@Argument Long categoryId, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findByCategoryId(categoryId, pageable);
        return EventPageDTO.from(eventPage);
    }

    @QueryMapping
    public EventPageDTO availableEvents(@Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findEventsWithAvailableTickets(pageable);
        return EventPageDTO.from(eventPage);
    }

    @QueryMapping
    public List<Event> topEvents(@Argument int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return eventRepository.findTopEventsByTicketsSold(pageable);
    }

    @QueryMapping
    public EventPageDTO searchEvents(@Argument String query, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.searchByTitleOrDescription(query, pageable);
        return EventPageDTO.from(eventPage);
    }

    // ==================== ORDER QUERIES ====================

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public OrderPageDTO myOrders(@AuthenticationPrincipal User user, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findByUserId(user.getId(), pageable);
        return OrderPageDTO.from(orderPage);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Order order(@AuthenticationPrincipal User user, @Argument UUID id) {
        Order order = orderRepository.findByIdWithDetails(id).orElse(null);
        if (order == null) return null;

        // Verify ownership unless admin
        if (!order.getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            return null;
        }
        return order;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Order orderByNumber(@AuthenticationPrincipal User user, @Argument String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);
        if (order == null) return null;

        if (!order.getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            return null;
        }
        return order;
    }

    // ==================== TICKET QUERIES ====================

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Ticket> myTickets(@AuthenticationPrincipal User user, @Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ticketRepository.findByUserId(user.getId(), pageable).getContent();
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Ticket ticket(@AuthenticationPrincipal User user, @Argument UUID id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) return null;

        if (!ticket.getOrder().getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            return null;
        }
        return ticket;
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public Ticket ticketByCode(@Argument String ticketCode) {
        return ticketRepository.findByTicketCode(ticketCode).orElse(null);
    }
}
