package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.BlockedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
public interface BlockedSlotRepository extends JpaRepository<BlockedSlot, UUID> { List<BlockedSlot> findByCourtIdAndBlockedDate(UUID courtId, LocalDate date); }
