package com.gsdeveloper.bookmyslot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerProfileRequest(
        @NotBlank @Size(max = 255) String fullName,
        @Email @NotBlank @Size(max = 255) String email,
        @Size(max = 255) String mobile,
        @Size(max = 2000) String avatarUrl) { }
