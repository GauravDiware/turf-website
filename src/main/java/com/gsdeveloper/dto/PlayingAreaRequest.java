package com.gsdeveloper.bookmyslot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PlayingAreaRequest(
        @NotBlank String name,
        @NotBlank String sport,
        String description,
        @PositiveOrZero Integer capacity,
        String surfaceType,
        String indoorOutdoor,
        @NotNull @DecimalMin(value = "0.00") BigDecimal basePrice,
        Boolean active
) { }
