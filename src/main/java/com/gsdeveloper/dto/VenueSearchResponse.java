package com.gsdeveloper.bookmyslot.dto;

import com.gsdeveloper.bookmyslot.entity.Venue;
import java.util.List;
import java.util.UUID;

/** Public venue payload used by the live search results. */
public record VenueSearchResponse(UUID id, String name, String description, String venueType, String address,
        String city, String state, String pincode, String contactNumber, String openingTime, String closingTime,
        String imageUrl, String status, List<String> registeredSports, List<PlayingAreaResponse> playingAreas) {
    public static VenueSearchResponse from(Venue venue, List<PlayingAreaResponse> areas) {
        return new VenueSearchResponse(venue.getId(), venue.getName(), venue.getDescription(), venue.getVenueType(), venue.getAddress(),
                venue.getCity(), venue.getState(), venue.getPincode(), venue.getContactNumber(),
                venue.getOpeningTime() == null ? null : venue.getOpeningTime().toString(), venue.getClosingTime() == null ? null : venue.getClosingTime().toString(),
                venue.getImageUrl(), venue.getStatus().name(), List.copyOf(venue.getRegisteredSports()), areas);
    }
}
