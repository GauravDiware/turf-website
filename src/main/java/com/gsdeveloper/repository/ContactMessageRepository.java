package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface ContactMessageRepository extends JpaRepository<ContactMessage, UUID> { }
