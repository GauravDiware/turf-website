package com.gsdeveloper.bookmyslot.controller;

import com.gsdeveloper.bookmyslot.dto.PriceCalculationRequest;
import com.gsdeveloper.bookmyslot.dto.PriceResponse;
import com.gsdeveloper.bookmyslot.dto.AvailabilityResponse;
import com.gsdeveloper.bookmyslot.dto.PlayingAreaResponse;
import com.gsdeveloper.bookmyslot.entity.Booking;
import com.gsdeveloper.bookmyslot.enums.BookingStatus;
import com.gsdeveloper.bookmyslot.repository.BookingRepository;
import com.gsdeveloper.bookmyslot.entity.Venue;
import com.gsdeveloper.bookmyslot.service.PricingService;
import com.gsdeveloper.bookmyslot.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import com.gsdeveloper.bookmyslot.dto.VenueSearchResponse;
import com.gsdeveloper.bookmyslot.dto.VenueFilterOptionsResponse;

@RestController @RequestMapping("/api/venues")
public class VenuePublicController {
    private final VenueService venues; private final PricingService pricing; private final BookingRepository bookings;
    public VenuePublicController(VenueService venues, PricingService pricing, BookingRepository bookings) { this.venues = venues; this.pricing = pricing; this.bookings = bookings; }
    @GetMapping public List<VenueSearchResponse> list(@RequestParam(required = false) String search, @RequestParam(required = false) String city,
                                                       @RequestParam(required = false) String game, @RequestParam(required = false, defaultValue = "recommended") String sort) {
        return venues.searchPublicVenues(search, city, game, sort);
    }
    @GetMapping("/filters") public VenueFilterOptionsResponse filters() { return venues.publicFilterOptions(); }
    @PostMapping("/{venueId}/price") public PriceResponse price(@PathVariable UUID venueId, @Valid @RequestBody PriceCalculationRequest request) { return pricing.calculate(venueId, request); }
    @GetMapping("/{venueId}/availability") public List<AvailabilityResponse> availability(@PathVariable UUID venueId, @RequestParam LocalDate date) {
        return venues.publicVenueAreas(venueId).stream().map(area -> new AvailabilityResponse(area.getId(), date,
                bookings.findByPlayingAreaIdAndBookingDateAndBookingStatusOrderByStartTime(area.getId(), date, BookingStatus.CONFIRMED)
                        .stream().map(booking -> new AvailabilityResponse.Slot(booking.getStartTime(), booking.getEndTime())).toList())).toList();
    }
    @GetMapping("/{venueId}/areas") public List<PlayingAreaResponse> areas(@PathVariable UUID venueId) {
        return venues.publicVenueAreas(venueId).stream().map(PlayingAreaResponse::from).toList();
    }
}
