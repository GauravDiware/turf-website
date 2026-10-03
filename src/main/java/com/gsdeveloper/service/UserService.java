package com.gsdeveloper.bookmyslot.service;

import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.entity.Profile;
import com.gsdeveloper.bookmyslot.enums.Role;
import com.gsdeveloper.bookmyslot.repository.UserRepository;
import com.gsdeveloper.bookmyslot.repository.ProfileRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
    }

    public User saveUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists with this email");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CLIENT);
        User savedUser = userRepository.save(user);
        if (!profileRepository.existsById(savedUser.getId())) {
            Profile profile = new Profile();
            profile.setUser(savedUser);
            profileRepository.save(profile);
        }
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void deleteUser(String email) {
        userRepository.deleteByEmail(email);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean emailExists(String email) { return userRepository.existsByEmail(email); }
}
