package com.gsdeveloper.bookmyslot.controller;

import com.gsdeveloper.bookmyslot.entity.Booking;
import com.gsdeveloper.bookmyslot.entity.Sport;
import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.entity.Venue;
import com.gsdeveloper.bookmyslot.enums.BookingStatus;
import com.gsdeveloper.bookmyslot.enums.Role;
import com.gsdeveloper.bookmyslot.enums.VenueStatus;
import com.gsdeveloper.bookmyslot.repository.BookingRepository;
import com.gsdeveloper.bookmyslot.repository.SportRepository;
import com.gsdeveloper.bookmyslot.repository.UserRepository;
import com.gsdeveloper.bookmyslot.repository.VenueRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** Platform operations. All routes are protected by hasRole(ADMIN) in SpringSecurity. */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users; private final VenueRepository venues; private final BookingRepository bookings; private final SportRepository sports;
    public AdminController(UserRepository users, VenueRepository venues, BookingRepository bookings, SportRepository sports) { this.users = users; this.venues = venues; this.bookings = bookings; this.sports = sports; }

    @GetMapping("/dashboard") public Dashboard dashboard() {
        List<Booking> all = bookings.findAll();
        BigDecimal revenue = all.stream().filter(b -> b.getBookingStatus() == BookingStatus.CONFIRMED).map(Booking::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        long today = all.stream().filter(b -> LocalDate.now().equals(b.getBookingDate()) && b.getBookingStatus() == BookingStatus.CONFIRMED).count();
        return new Dashboard(users.count(), venues.count(), users.countByRole(Role.CUSTOMER), today, revenue, venues.countByStatus(VenueStatus.PENDING));
    }
    @GetMapping("/venues") public List<VenueView> listVenues() { return venues.findAll().stream().sorted(Comparator.comparing(Venue::getCreatedAt).reversed()).map(this::venue).toList(); }
    @PatchMapping("/venues/{id}/status") public VenueView changeVenueStatus(@PathVariable UUID id, @RequestBody StatusRequest request) {
        Venue value = venues.findById(id).orElseThrow(() -> new NotFoundException("Venue not found"));
        try { value.setStatus(VenueStatus.valueOf(request.status().toUpperCase())); } catch (Exception e) { throw new IllegalArgumentException("Invalid venue status"); }
        return venue(venues.save(value));
    }
    @GetMapping("/customers") public List<UserView> listCustomers() { return users.findAll().stream().filter(u -> u.getRole() == Role.CUSTOMER).map(this::user).toList(); }
    @GetMapping("/bookings") public List<BookingView> listBookings() { return bookings.findAll().stream().sorted(Comparator.comparing(Booking::getCreatedAt).reversed()).map(this::booking).toList(); }
    @PatchMapping("/bookings/{id}/cancel") public BookingView cancel(@PathVariable UUID id) { Booking value = bookings.findById(id).orElseThrow(() -> new NotFoundException("Booking not found")); value.setBookingStatus(BookingStatus.CANCELLED); return booking(bookings.save(value)); }
    @GetMapping("/sports") public List<SportView> listSports() { return sports.findAll().stream().map(this::sport).toList(); }
    @PostMapping("/sports") @ResponseStatus(HttpStatus.CREATED) public SportView createSport(@RequestBody SportRequest request) {
        if (request.name() == null || request.name().isBlank()) throw new IllegalArgumentException("Sport name is required");
        if (sports.existsByNameIgnoreCase(request.name().trim())) throw new IllegalArgumentException("A sport with this name already exists");
        Sport value = new Sport(); value.setName(request.name().trim()); value.setDescription(request.description()); value.setActive(true); return sport(sports.save(value));
    }
    @PatchMapping("/sports/{id}") public SportView changeSport(@PathVariable UUID id, @RequestBody SportRequest request) {
        Sport value = sports.findById(id).orElseThrow(() -> new NotFoundException("Sport not found"));
        if (request.name() != null && !request.name().isBlank()) value.setName(request.name().trim()); if (request.description() != null) value.setDescription(request.description()); if (request.active() != null) value.setActive(request.active()); return sport(sports.save(value));
    }
    private VenueView venue(Venue v) { return new VenueView(v.getId(), v.getName(), v.getOwner().getFullName(), v.getOwner().getEmail(), v.getCity(), v.getStatus().name(), v.getCreatedAt()); }
    private UserView user(User u) { return new UserView(u.getId(), u.getFullName(), u.getEmail(), u.getMobile()); }
    private BookingView booking(Booking b) { String venue = b.getPlayingArea() != null ? b.getPlayingArea().getVenue().getName() : b.getTurf() != null ? b.getTurf().getName() : "—"; return new BookingView(b.getId(), b.getUser().getFullName(), b.getUser().getEmail(), venue, b.getBookingDate(), b.getStartTime().toString(), b.getEndTime().toString(), b.getTotalPrice(), b.getBookingStatus().name()); }
    private SportView sport(Sport s) { return new SportView(s.getId(), s.getName(), s.getDescription(), s.isActive()); }
    public record Dashboard(long totalUsers, long totalVenues, long totalCustomers, long todayBookings, BigDecimal totalRevenue, long pendingVenues) {}
    public record VenueView(UUID id, String name, String ownerName, String ownerEmail, String city, String status, LocalDateTime createdAt) {}
    public record UserView(UUID id, String fullName, String email, String mobile) {}
    public record BookingView(UUID id, String customerName, String customerEmail, String venueName, LocalDate date, String startTime, String endTime, BigDecimal totalPrice, String status) {}
    public record SportView(UUID id, String name, String description, boolean active) {}
    public record StatusRequest(String status) {}
    public record SportRequest(String name, String description, Boolean active) {}
    @ResponseStatus(HttpStatus.NOT_FOUND) static class NotFoundException extends RuntimeException { NotFoundException(String message) { super(message); } }
}
