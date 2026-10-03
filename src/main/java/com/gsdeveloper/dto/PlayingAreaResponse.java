package com.gsdeveloper.bookmyslot.dto;

import com.gsdeveloper.bookmyslot.entity.PlayingArea;

import java.math.BigDecimal;
import java.util.UUID;

public record PlayingAreaResponse(UUID id, String name, String sport, BigDecimal basePrice, Integer capacity, String surfaceType, boolean active) {
    public static PlayingAreaResponse from(PlayingArea area) {
        return new PlayingAreaResponse(area.getId(), area.getName(), area.getSport().getName(), area.getBasePrice(), area.getCapacity(), area.getSurfaceType(), area.isActive());
    }
}
