package com.gsdeveloper.bookmyslot.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record PriceResponse(UUID venueId, UUID playingAreaId, BigDecimal hourlyPrice, BigDecimal totalAmount, String ruleApplied) { }
