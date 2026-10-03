package com.gsdeveloper.bookmyslot.service;

import com.gsdeveloper.bookmyslot.dto.VenueRegistrationRequest;
import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.entity.Venue;
import com.gsdeveloper.bookmyslot.entity.VenueRegistration;
import com.gsdeveloper.bookmyslot.enums.Role;
import com.gsdeveloper.bookmyslot.enums.VenueStatus;
import com.gsdeveloper.bookmyslot.repository.UserRepository;
import com.gsdeveloper.bookmyslot.repository.VenueRepository;
import com.gsdeveloper.bookmyslot.repository.PlayingAreaRepository;
import com.gsdeveloper.bookmyslot.repository.VenueRegistrationRepository;
import com.gsdeveloper.bookmyslot.repository.SportRepository;
import com.gsdeveloper.bookmyslot.dto.PlayingAreaRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.Locale;
import java.util.TreeMap;
import java.util.stream.Collectors;
import com.gsdeveloper.bookmyslot.dto.PlayingAreaResponse;
import com.gsdeveloper.bookmyslot.dto.VenueFilterOptionsResponse;
import com.gsdeveloper.bookmyslot.dto.VenueSearchResponse;
import com.gsdeveloper.bookmyslot.dto.CityFilterOption;

@Service
public class VenueService {
    private final UserRepository users; private final VenueRepository venues; private final PasswordEncoder encoder; private final PlayingAreaRepository areas; private final VenueRegistrationRepository registrations; private final SportRepository sports;
    public VenueService(UserRepository users, VenueRepository venues, PasswordEncoder encoder, PlayingAreaRepository areas, VenueRegistrationRepository registrations, SportRepository sports) { this.users = users; this.venues = venues; this.encoder = encoder; this.areas = areas; this.registrations = registrations; this.sports = sports; }
    @Transactional
    public Venue register(VenueRegistrationRequest request) {
        if (users.existsByEmail(request.email().trim().toLowerCase())) throw conflict("Email is already registered");
        if (users.existsByMobile(request.mobile())) throw conflict("Mobile is already registered");
        User user = new User(); user.setFullName(request.fullName().trim()); user.setEmail(request.email().trim().toLowerCase()); user.setMobile(request.mobile()); user.setPassword(encoder.encode(request.password())); user.setRole(Role.VENUE_OWNER); users.save(user);
        Venue venue = new Venue(); venue.setOwner(user); venue.setName(request.venueName()); venue.setAddress(request.address()); venue.setCity(request.city()); venue.setState(request.state()); venue.setPincode(request.pincode()); venue.setVenueType(request.venueType()); venue.setContactNumber(request.contactNumber()); venue.setEmail(user.getEmail()); venue.setOpeningTime(request.openingTime()); venue.setClosingTime(request.closingTime()); venue.setImageUrl(blankToNull(request.imageUrl())); venue.setRegisteredSports(normalizeSports(request.sports())); venue.setStatus(VenueStatus.ACTIVE);
        Venue savedVenue = venues.save(venue);
        VenueRegistration registration = new VenueRegistration();
        registration.setOwner(user); registration.setFullName(request.fullName()); registration.setEmail(request.email().trim().toLowerCase()); registration.setMobile(request.mobile()); registration.setVenueName(request.venueName()); registration.setAddress(request.address()); registration.setCity(request.city()); registration.setState(request.state()); registration.setPincode(request.pincode());
        registration.setStatus(VenueStatus.ACTIVE.name());
        registrations.save(registration);
        return savedVenue;
    }
    public List<Venue> myVenues(String email) { return venues.findByOwnerEmail(email); }
    public List<com.gsdeveloper.bookmyslot.entity.Sport> activeSports() { return sports.findByActiveTrue(); }
    public List<Venue> publicVenues() { return venues.findByStatusIn(List.of(VenueStatus.APPROVED, VenueStatus.ACTIVE)); }
    public List<VenueSearchResponse> searchPublicVenues(String search, String city, String game, String sort) {
        List<Venue> matchingVenues = venues.searchPublicVenues(clean(search), clean(city), clean(game), normalizeSort(sort), publicStatuses());
        if (matchingVenues.isEmpty()) return List.of();
        Map<UUID, List<PlayingAreaResponse>> areasByVenue = areas.findPublicAreasByVenueIds(matchingVenues.stream().map(Venue::getId).toList()).stream()
                .collect(Collectors.groupingBy(area -> area.getVenue().getId(), Collectors.mapping(PlayingAreaResponse::from, Collectors.toList())));
        return matchingVenues.stream().map(venue -> VenueSearchResponse.from(venue, areasByVenue.getOrDefault(venue.getId(), List.of()))).toList();
    }
    public VenueFilterOptionsResponse publicFilterOptions() {
        List<CityFilterOption> cities = venues.findPublicCityCounts(publicStatuses()).stream()
                .map(value -> new CityFilterOption(value.getCity(), value.getTurfCount()))
                .sorted(java.util.Comparator.comparing(option -> option.city().toLowerCase(Locale.ROOT))).toList();
        return new VenueFilterOptionsResponse(cities, sortedUnique(venues.findPublicRegisteredSports(publicStatuses())));
    }
    public List<com.gsdeveloper.bookmyslot.entity.PlayingArea> publicVenueAreas(UUID venueId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
        if (venue.getStatus() != VenueStatus.APPROVED && venue.getStatus() != VenueStatus.ACTIVE) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found");
        return areas.findByVenueIdAndActiveTrue(venueId);
    }
    @Transactional public Venue changeStatus(UUID id, VenueStatus status) { Venue venue = venues.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found")); venue.setStatus(status); return venue; }
    public Venue ownedVenue(UUID id, String email) { return venues.findByIdAndOwnerEmail(id, email).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Venue does not belong to the authenticated owner")); }
    public List<com.gsdeveloper.bookmyslot.entity.PlayingArea> ownedVenueAreas(UUID venueId, String email) { ownedVenue(venueId, email); return areas.findAll().stream().filter(area -> area.getVenue().getId().equals(venueId)).toList(); }
    @Transactional public com.gsdeveloper.bookmyslot.entity.PlayingArea createArea(UUID venueId, String email, PlayingAreaRequest request) { com.gsdeveloper.bookmyslot.entity.PlayingArea area = new com.gsdeveloper.bookmyslot.entity.PlayingArea(); area.setVenue(ownedVenue(venueId, email)); applyArea(area, request); return areas.save(area); }
    @Transactional public com.gsdeveloper.bookmyslot.entity.PlayingArea updateArea(UUID venueId, UUID areaId, String email, PlayingAreaRequest request) { com.gsdeveloper.bookmyslot.entity.PlayingArea area = areas.findByIdAndVenueOwnerEmail(areaId, email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playing area not found")); if (!area.getVenue().getId().equals(venueId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playing area not found"); applyArea(area, request); return area; }
    @Transactional public void deactivateArea(UUID venueId, UUID areaId, String email) { com.gsdeveloper.bookmyslot.entity.PlayingArea area = areas.findByIdAndVenueOwnerEmail(areaId, email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playing area not found")); if (!area.getVenue().getId().equals(venueId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playing area not found"); area.setActive(false); }
    private void applyArea(com.gsdeveloper.bookmyslot.entity.PlayingArea area, PlayingAreaRequest request) { area.setName(request.name().trim()); area.setSport(sports.findByNameIgnoreCase(request.sport().trim()).filter(com.gsdeveloper.bookmyslot.entity.Sport::isActive).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sport is not available"))); area.setDescription(request.description()); area.setCapacity(request.capacity()); area.setSurfaceType(request.surfaceType()); area.setIndoorOutdoor(request.indoorOutdoor()); area.setBasePrice(request.basePrice()); if (request.active() != null) area.setActive(request.active()); }
    private ResponseStatusException conflict(String message) { return new ResponseStatusException(HttpStatus.CONFLICT, message); }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private java.util.Set<String> normalizeSports(List<String> selectedSports) {
        if (selectedSports == null) return new java.util.LinkedHashSet<>();
        return selectedSports.stream().filter(value -> value != null && !value.isBlank()).map(String::trim)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }
    private List<String> sortedUnique(List<String> values) {
        return new java.util.ArrayList<>(values.stream().filter(value -> value != null && !value.isBlank())
                .collect(Collectors.toMap(value -> value.trim().toLowerCase(Locale.ROOT), String::trim, (first, ignored) -> first, TreeMap::new)).values());
    }
    private List<VenueStatus> publicStatuses() { return List.of(VenueStatus.APPROVED, VenueStatus.ACTIVE); }
    private String normalizeSort(String sort) {
        String candidate = sort == null ? "recommended" : sort.trim().toLowerCase();
        return switch (candidate) { case "price-low", "price-high", "name", "location", "recommended" -> candidate; default -> "recommended"; };
    }
}
