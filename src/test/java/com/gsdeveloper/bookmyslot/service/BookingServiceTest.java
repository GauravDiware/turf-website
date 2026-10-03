package com.gsdeveloper.bookmyslot.service;

import com.gsdeveloper.bookmyslot.dto.CreateBookingRequest;
import com.gsdeveloper.bookmyslot.entity.Booking;
import com.gsdeveloper.bookmyslot.entity.Turf;
import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.enums.BookingStatus;
import com.gsdeveloper.bookmyslot.repository.BookingRepository;
import com.gsdeveloper.bookmyslot.repository.TurfRepository;
import com.gsdeveloper.bookmyslot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TurfRepository turfRepository;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, userRepository, turfRepository);
    }

    @Test
    void createsConfirmedBookingWithCalculatedPrice() {
        UUID turfId = UUID.randomUUID();
        User user = new User();
        user.setEmail("client@example.com");
        Turf turf = new Turf();
        turf.setId(turfId);
        turf.setAvailable(true);
        turf.setPricePerHour(600.0);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(turfRepository.findById(turfId)).thenReturn(Optional.of(turf));
        when(bookingRepository.existsOverlappingBooking(any(), any(), any(), any(), eq(BookingStatus.CONFIRMED)))
                .thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.createBooking(user.getEmail(), request(turfId, "18:00", "19:30"));

        assertThat(booking.getUser()).isSameAs(user);
        assertThat(booking.getTurf()).isSameAs(turf);
        assertThat(booking.getBookingStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.getTotalPrice()).isEqualByComparingTo(new BigDecimal("900.00"));
    }

    @Test
    void rejectsAnOverlappingConfirmedBooking() {
        UUID turfId = UUID.randomUUID();
        User user = new User();
        user.setEmail("client@example.com");
        Turf turf = new Turf();
        turf.setId(turfId);
        turf.setAvailable(true);
        turf.setPricePerHour(600.0);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(turfRepository.findById(turfId)).thenReturn(Optional.of(turf));
        when(bookingRepository.existsOverlappingBooking(any(), any(), any(), any(), eq(BookingStatus.CONFIRMED)))
                .thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(user.getEmail(), request(turfId, "18:00", "19:00")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void onlyBookingOwnerCanCancel() {
        UUID bookingId = UUID.randomUUID();
        User owner = new User();
        owner.setEmail("owner@example.com");
        Booking booking = new Booking();
        booking.setUser(owner);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(owner.getEmail(), bookingId);

        assertThat(booking.getBookingStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    private CreateBookingRequest request(UUID turfId, String startTime, String endTime) {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setTurfId(turfId);
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setStartTime(LocalTime.parse(startTime));
        request.setEndTime(LocalTime.parse(endTime));
        return request;
    }
}
