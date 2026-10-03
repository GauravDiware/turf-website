package com.gsdeveloper.bookmyslot.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record AvailabilityResponse(UUID playingAreaId, LocalDate date, List<Slot> bookedSlots) {
    public record Slot(LocalTime startTime, LocalTime endTime) { }
}
