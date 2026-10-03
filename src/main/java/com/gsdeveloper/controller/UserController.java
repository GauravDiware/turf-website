package com.gsdeveloper.bookmyslot.controller;

import com.gsdeveloper.bookmyslot.dto.CustomerProfileResponse;
import com.gsdeveloper.bookmyslot.dto.UpdateCustomerProfileRequest;
import com.gsdeveloper.bookmyslot.entity.Profile;
import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.repository.ProfileRepository;
import com.gsdeveloper.bookmyslot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ProfileRepository profiles;

    public UserController(UserService userService, ProfileRepository profiles) {
        this.userService = userService;
        this.profiles = profiles;
    }
    @GetMapping
    public CustomerProfileResponse getUser() {
        User user = currentUser();
        return CustomerProfileResponse.from(user, profileFor(user));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> updateUser(@Valid @RequestBody UpdateCustomerProfileRequest request) {
        User user = currentUser();
        String email = request.email().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(email) && userService.emailExists(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setMobile(blankToNull(request.mobile()));
        User saved = userService.updateUser(user);
        Profile profile = profileFor(saved);
        profile.setAvatarUrl(blankToNull(request.avatarUrl()));
        profiles.save(profile);
        return ResponseEntity.ok(CustomerProfileResponse.from(saved, profile));
    }

    private User currentUser() { return userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()); }
    private Profile profileFor(User user) { return profiles.findById(user.getId()).orElseGet(() -> { Profile profile = new Profile(); profile.setUser(user); return profiles.save(profile); }); }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }

}
