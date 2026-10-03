package com.gsdeveloper.bookmyslot.service;

import com.gsdeveloper.bookmyslot.dto.PriceCalculationRequest;
import com.gsdeveloper.bookmyslot.dto.PriceResponse;
import com.gsdeveloper.bookmyslot.entity.PlayingArea;
import com.gsdeveloper.bookmyslot.entity.PricingRule;
import com.gsdeveloper.bookmyslot.enums.PricingRuleType;
import com.gsdeveloper.bookmyslot.enums.VenueStatus;
import com.gsdeveloper.bookmyslot.repository.PlayingAreaRepository;
import com.gsdeveloper.bookmyslot.repository.PricingRuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.*;
import java.time.Duration;
import java.util.Comparator;
import java.util.UUID;

@Service
public class PricingService {
    private final PlayingAreaRepository areas; private final PricingRuleRepository rules;
    public PricingService(PlayingAreaRepository areas, PricingRuleRepository rules) { this.areas = areas; this.rules = rules; }
    public PriceResponse calculate(UUID venueId, PriceCalculationRequest request) {
        if (!request.startTime().isBefore(request.endTime())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
        PlayingArea area = areas.findById(request.playingAreaId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playing area not found"));
        if (!area.getVenue().getId().equals(venueId) || !area.isActive()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playing area not available for venue");
        if (area.getVenue().getStatus() != VenueStatus.APPROVED && area.getVenue().getStatus() != VenueStatus.ACTIVE) throw new ResponseStatusException(HttpStatus.CONFLICT, "Venue is not publicly bookable");
        PricingRule winner = rules.findByPlayingAreaIdAndActiveTrue(area.getId()).stream().filter(rule -> matches(rule, request)).max(Comparator.comparingInt(PricingRule::getPriority)).orElse(null);
        BigDecimal hourly = winner == null ? area.getBasePrice() : winner.getPrice();
        BigDecimal total = hourly.multiply(BigDecimal.valueOf(Duration.between(request.startTime(), request.endTime()).toMinutes())).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return new PriceResponse(venueId, area.getId(), hourly, total, winner == null ? "BASE" : winner.getRuleType().name());
    }
    private boolean matches(PricingRule rule, PriceCalculationRequest request) {
        boolean dateMatches = rule.getSpecificDate() == null || rule.getSpecificDate().equals(request.date());
        boolean dayMatches = rule.getDayOfWeek() == null || rule.getDayOfWeek().equals(request.date().getDayOfWeek());
        return dateMatches && dayMatches && !request.startTime().isBefore(rule.getStartTime()) && !request.endTime().isAfter(rule.getEndTime());
    }
}
