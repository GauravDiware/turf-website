package com.gsdeveloper.bookmyslot.controller;

import com.gsdeveloper.bookmyslot.dto.FavoriteResponse;
import com.gsdeveloper.bookmyslot.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService favorites;

    public FavoriteController(FavoriteService favorites) {
        this.favorites = favorites;
    }

    @GetMapping
    public List<FavoriteResponse> list() {
        return favorites.list(email());
    }

    @PostMapping("/{venueId}")
    public ResponseEntity<FavoriteResponse> add(@PathVariable UUID venueId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(favorites.add(email(), venueId));
    }

    @DeleteMapping("/{venueId}")
    public ResponseEntity<Void> remove(@PathVariable UUID venueId) {
        favorites.remove(email(), venueId);
        return ResponseEntity.noContent().build();
    }

    private String email() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
