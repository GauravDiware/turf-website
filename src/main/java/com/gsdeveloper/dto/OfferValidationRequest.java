package com.gsdeveloper.bookmyslot.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
public record OfferValidationRequest(@NotBlank String couponCode, @NotNull UUID venueId, @NotNull UUID playingAreaId, @NotNull LocalDate bookingDate, @NotNull LocalTime startTime, @NotNull LocalTime endTime, @NotNull BigDecimal bookingAmount) { }
