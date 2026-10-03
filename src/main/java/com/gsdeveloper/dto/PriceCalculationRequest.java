package com.gsdeveloper.bookmyslot.dto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
public record PriceCalculationRequest(@NotNull UUID playingAreaId, @NotNull LocalDate date, @NotNull LocalTime startTime, @NotNull LocalTime endTime) { }
