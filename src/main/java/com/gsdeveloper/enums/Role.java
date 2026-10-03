package com.gsdeveloper.bookmyslot.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Role {
    ADMIN,
    CLIENT,
    CUSTOMER,
    VENUE_OWNER;

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(role -> role.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + value));
    }
}
