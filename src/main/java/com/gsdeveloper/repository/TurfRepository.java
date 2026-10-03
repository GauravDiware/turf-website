package com.gsdeveloper.bookmyslot.repository;

import com.gsdeveloper.bookmyslot.entity.Turf;
import com.gsdeveloper.bookmyslot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TurfRepository extends JpaRepository<Turf, UUID> {


    boolean existsByNameAndLocation(String name, String location);

}
