package com.gsdeveloper.bookmyslot.dto;

import com.gsdeveloper.bookmyslot.entity.Favorite;

import java.time.LocalDateTime;
import java.util.UUID;

public record FavoriteResponse(
        UUID id,
        UUID venueId,
        String venueName,
        String city,
        String venueStatus,
        LocalDateTime createdAt) {

    public static FavoriteResponse from(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getVenue().getId(),
                favorite.getVenue().getName(),
                favorite.getVenue().getCity(),
                favorite.getVenue().getStatus().name(),
                favorite.getCreatedAt());
    }
}
