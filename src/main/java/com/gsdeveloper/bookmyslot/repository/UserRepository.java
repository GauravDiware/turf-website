package com.gsdeveloper.bookmyslot.repository;

import com.gsdeveloper.bookmyslot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}