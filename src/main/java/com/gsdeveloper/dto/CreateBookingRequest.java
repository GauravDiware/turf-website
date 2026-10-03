package com.gsdeveloper.bookmyslot.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class CreateBookingRequest {

    private UUID playingAreaId;

    private UUID turfId;

    public UUID getPlayingAreaId() {
        return playingAreaId;
    }

    public void setPlayingAreaId(UUID playingAreaId) {
        this.playingAreaId = playingAreaId;
    }

    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date cannot be in the past")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    public UUID getTurfId() {
        return turfId;
    }

    public void setTurfId(UUID turfId) {
        this.turfId = turfId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
