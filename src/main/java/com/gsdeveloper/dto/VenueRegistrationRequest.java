package com.gsdeveloper.bookmyslot.dto;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.List;

public record VenueRegistrationRequest(
        @NotBlank String fullName, @Email @NotBlank String email,
        @NotBlank @Pattern(regexp = "^[0-9]{10,15}$", message = "mobile must contain 10 to 15 digits") String mobile,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank String venueName, @NotBlank String address, @NotBlank String city,
        @NotBlank @Pattern(regexp = "^[0-9]{6}$", message = "pincode must contain 6 digits") String pincode,
        String state, String venueType, String contactNumber, LocalTime openingTime, LocalTime closingTime, String imageUrl,
        List<@NotBlank String> sports) { }
