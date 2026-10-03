package com.gsdeveloper.bookmyslot.service;

import com.gsdeveloper.bookmyslot.dto.CreateBookingRequest;
import com.gsdeveloper.bookmyslot.entity.Booking;
import com.gsdeveloper.bookmyslot.entity.Turf;
import com.gsdeveloper.bookmyslot.entity.PlayingArea;
import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.enums.BookingStatus;
import com.gsdeveloper.bookmyslot.repository.BookingRepository;
import com.gsdeveloper.bookmyslot.repository.TurfRepository;
import com.gsdeveloper.bookmyslot.repository.PlayingAreaRepository;
import com.gsdeveloper.bookmyslot.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TurfRepository turfRepository;
    private final PlayingAreaRepository playingAreaRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          TurfRepository turfRepository,
                          PlayingAreaRepository playingAreaRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.turfRepository = turfRepository;
        this.playingAreaRepository = playingAreaRepository;
    }

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, TurfRepository turfRepository) {
        this(bookingRepository, userRepository, turfRepository, null);
    }

    @Transactional
    public Booking createBooking(String email, CreateBookingRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (request.getPlayingAreaId() != null) {
            return createPlayingAreaBooking(user, request);
        }

        if (request.getTurfId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A playing area or turf id is required");
        }

        Turf turf = turfRepository.findById(request.getTurfId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turf not found"));

        if (!turf.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Turf is not available");
        }

        boolean overlapping = bookingRepository.existsOverlappingBooking(
                turf.getId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime(),
                BookingStatus.CONFIRMED
        );
        if (overlapping) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot already booked");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTurf(turf);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setTotalPrice(calculatePrice(turf, request));
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    private Booking createPlayingAreaBooking(User user, CreateBookingRequest request) {
        if (playingAreaRepository == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Playing-area booking is not configured");
        }
        PlayingArea area = playingAreaRepository.findById(request.getPlayingAreaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playing area not found"));
        if (!area.isActive() || area.getVenue().getStatus() == null ||
                (area.getVenue().getStatus() != com.gsdeveloper.bookmyslot.enums.VenueStatus.APPROVED &&
                 area.getVenue().getStatus() != com.gsdeveloper.bookmyslot.enums.VenueStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Playing area is not bookable");
        }
        if (bookingRepository.existsOverlappingPlayingAreaBooking(area.getId(), request.getBookingDate(), request.getStartTime(), request.getEndTime(), BookingStatus.CONFIRMED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slot already booked");
        }
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setPlayingArea(area);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        booking.setTotalPrice(area.getBasePrice().multiply(BigDecimal.valueOf(minutes)).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(String email) {
        return bookingRepository.findByUserEmailOrderByBookingDateDescStartTimeDesc(email);
    }

    @Transactional
    public void cancelBooking(String email, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!booking.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot cancel this booking");
        }
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is already cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
    }

    private BigDecimal calculatePrice(Turf turf, CreateBookingRequest request) {
        if (turf.getPricePerHour() == null || turf.getPricePerHour() < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Turf has an invalid hourly price");
        }

        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        return BigDecimal.valueOf(turf.getPricePerHour())
                .multiply(BigDecimal.valueOf(minutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}
