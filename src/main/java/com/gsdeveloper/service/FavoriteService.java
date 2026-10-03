package com.gsdeveloper.bookmyslot.service;

import com.gsdeveloper.bookmyslot.dto.FavoriteResponse;
import com.gsdeveloper.bookmyslot.entity.Favorite;
import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.entity.Venue;
import com.gsdeveloper.bookmyslot.enums.VenueStatus;
import com.gsdeveloper.bookmyslot.repository.FavoriteRepository;
import com.gsdeveloper.bookmyslot.repository.UserRepository;
import com.gsdeveloper.bookmyslot.repository.VenueRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class FavoriteService {
    private final FavoriteRepository favorites;
    private final UserRepository users;
    private final VenueRepository venues;

    public FavoriteService(FavoriteRepository favorites, UserRepository users, VenueRepository venues) {
        this.favorites = favorites;
        this.users = users;
        this.venues = venues;
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> list(String email) {
        User user = user(email);
        return favorites.findAllByUserIdWithVenue(user.getId()).stream().map(FavoriteResponse::from).toList();
    }

    @Transactional
    public FavoriteResponse add(String email, UUID venueId) {
        User user = user(email);
        Venue venue = venues.findById(venueId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
        if (venue.getStatus() != VenueStatus.APPROVED && venue.getStatus() != VenueStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found");
        }
        Favorite favorite = favorites.findByUserIdAndVenueId(user.getId(), venueId)
                .orElseGet(() -> {
                    Favorite created = new Favorite();
                    created.setUser(user);
                    created.setVenue(venue);
                    return favorites.save(created);
                });
        return FavoriteResponse.from(favorite);
    }

    @Transactional
    public void remove(String email, UUID venueId) {
        if (favorites.deleteByUserIdAndVenueId(user(email).getId(), venueId) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found");
        }
    }

    private User user(String email) {
        return users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
