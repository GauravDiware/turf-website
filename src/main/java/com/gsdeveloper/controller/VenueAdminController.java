package com.gsdeveloper.bookmyslot.controller;

import com.gsdeveloper.bookmyslot.entity.Sport;
import com.gsdeveloper.bookmyslot.entity.Venue;
import com.gsdeveloper.bookmyslot.enums.VenueStatus;
import com.gsdeveloper.bookmyslot.repository.SportRepository;
import com.gsdeveloper.bookmyslot.repository.VenueRepository;
import com.gsdeveloper.bookmyslot.service.VenueService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * Legacy venue administration endpoints retained for backward compatibility.
 * New clients use AdminController under /api/admin.
 */
@RestController @RequestMapping("/api/admin/legacy")
public class VenueAdminController {
    private final VenueRepository venues; private final VenueService venueService; private final SportRepository sports;
    public VenueAdminController(VenueRepository venues, VenueService venueService, SportRepository sports) { this.venues = venues; this.venueService = venueService; this.sports = sports; }
    @GetMapping("/venues") public List<Venue> venues() { return venues.findAll(); }
    @GetMapping("/venues/{id}") public Venue venue(@PathVariable UUID id) { return venues.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found")); }
    @PutMapping("/venues/{id}/approve") public Venue approve(@PathVariable UUID id) { return venueService.changeStatus(id, VenueStatus.APPROVED); }
    @PutMapping("/venues/{id}/reject") public Venue reject(@PathVariable UUID id) { return venueService.changeStatus(id, VenueStatus.REJECTED); }
    @PutMapping("/venues/{id}/suspend") public Venue suspend(@PathVariable UUID id) { return venueService.changeStatus(id, VenueStatus.SUSPENDED); }
    @PutMapping("/venues/{id}/activate") public Venue activate(@PathVariable UUID id) { return venueService.changeStatus(id, VenueStatus.ACTIVE); }
    @GetMapping("/sports") public List<Sport> sports() { return sports.findAll(); }
    @PostMapping("/sports") @ResponseStatus(HttpStatus.CREATED) public Sport createSport(@RequestBody Sport sport) { if (sports.existsByNameIgnoreCase(sport.getName())) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT, "Sport already exists"); sport.setId(null); return sports.save(sport); }
}
