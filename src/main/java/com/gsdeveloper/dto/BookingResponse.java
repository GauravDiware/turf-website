package com.gsdeveloper.bookmyslot.dto;

import com.gsdeveloper.bookmyslot.entity.Booking;
import com.gsdeveloper.bookmyslot.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID turfId,
        String turfName,
        UUID playingAreaId,
        String playingAreaName,
        LocalDate bookingDate,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal totalPrice,
        BookingStatus bookingStatus,
        LocalDateTime createdAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getTurf() == null ? null : booking.getTurf().getId(),
                booking.getTurf() == null ? null : booking.getTurf().getName(),
                booking.getPlayingArea() == null ? null : booking.getPlayingArea().getId(),
                booking.getPlayingArea() == null ? null : booking.getPlayingArea().getName(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getTotalPrice(),
                booking.getBookingStatus(),
                booking.getCreatedAt()
        );
    }
}
