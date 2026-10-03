package com.gsdeveloper.bookmyslot.repository;

import com.gsdeveloper.bookmyslot.entity.Booking;
import com.gsdeveloper.bookmyslot.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("""
            select (count(b) > 0)
            from Booking b
            where b.turf.id = :turfId
              and b.bookingDate = :bookingDate
              and b.bookingStatus = :bookingStatus
              and :startTime < b.endTime
              and :endTime > b.startTime
            """)
    boolean existsOverlappingBooking(
            @Param("turfId") UUID turfId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("bookingStatus") BookingStatus bookingStatus
    );

    @Query("""
            select (count(b) > 0) from Booking b
            where b.playingArea.id = :playingAreaId
              and b.bookingDate = :bookingDate
              and b.bookingStatus = :bookingStatus
              and :startTime < b.endTime
              and :endTime > b.startTime
            """)
    boolean existsOverlappingPlayingAreaBooking(
            @Param("playingAreaId") UUID playingAreaId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("bookingStatus") BookingStatus bookingStatus
    );

    List<Booking> findByUserEmailOrderByBookingDateDescStartTimeDesc(String email);

        List<Booking> findByPlayingAreaIdAndBookingDateAndBookingStatusOrderByStartTime(
                        UUID playingAreaId, LocalDate bookingDate, BookingStatus bookingStatus);
}
