package com.gsdeveloper.bookmyslot.controller;

import com.gsdeveloper.bookmyslot.dto.*;
import com.gsdeveloper.bookmyslot.entity.Venue;
import com.gsdeveloper.bookmyslot.entity.Sport;
import com.gsdeveloper.bookmyslot.service.AuthService;
import com.gsdeveloper.bookmyslot.service.PricingService;
import com.gsdeveloper.bookmyslot.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/venue")
public class VenueController {
    private final VenueService venues; private final AuthService auth; private final PricingService pricing;
    public VenueController(VenueService venues, AuthService auth, PricingService pricing) { this.venues = venues; this.auth = auth; this.pricing = pricing; }
    @PostMapping("/register") public ResponseEntity<Venue> register(@Valid @RequestBody VenueRegistrationRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(venues.register(request)); }
    @PostMapping("/login") public ResponseEntity<?> login(@RequestBody AuthRequest request) { AuthResponse response = auth.login(request.getEmail(), request.getPassword()); return response == null ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password") : ResponseEntity.ok(response); }
    @GetMapping("/venues") public List<Venue> myVenues() { return venues.myVenues(email()); }
    @GetMapping("/sports") public List<Sport> sports() { return venues.activeSports(); }
    @GetMapping("/venues/{venueId}/areas") public List<PlayingAreaResponse> myAreas(@PathVariable UUID venueId) { return venues.ownedVenueAreas(venueId, email()).stream().map(PlayingAreaResponse::from).toList(); }
    @PostMapping("/venues/{venueId}/areas") public ResponseEntity<PlayingAreaResponse> createArea(@PathVariable UUID venueId, @Valid @RequestBody PlayingAreaRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(PlayingAreaResponse.from(venues.createArea(venueId, email(), request))); }
    @PutMapping("/venues/{venueId}/areas/{areaId}") public PlayingAreaResponse updateArea(@PathVariable UUID venueId, @PathVariable UUID areaId, @Valid @RequestBody PlayingAreaRequest request) { return PlayingAreaResponse.from(venues.updateArea(venueId, areaId, email(), request)); }
    @DeleteMapping("/venues/{venueId}/areas/{areaId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivateArea(@PathVariable UUID venueId, @PathVariable UUID areaId) { venues.deactivateArea(venueId, areaId, email()); }
    @PostMapping("/venues/{venueId}/price") public PriceResponse calculatePrice(@PathVariable UUID venueId, @Valid @RequestBody PriceCalculationRequest request) { venues.ownedVenue(venueId, email()); return pricing.calculate(venueId, request); }
    private String email() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
}
