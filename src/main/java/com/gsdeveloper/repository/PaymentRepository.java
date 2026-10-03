package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface PaymentRepository extends JpaRepository<Payment, UUID> { Optional<Payment> findByBookingId(UUID bookingId); }
