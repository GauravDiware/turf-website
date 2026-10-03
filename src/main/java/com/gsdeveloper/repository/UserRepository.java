package com.gsdeveloper.bookmyslot.repository;

import com.gsdeveloper.bookmyslot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.gsdeveloper.bookmyslot.enums.Role;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    List<User> email(String email);

    void deleteByEmail(String email);
    long countByRole(Role role);
}
