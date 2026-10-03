package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface MembershipRepository extends JpaRepository<Membership, UUID> { }
