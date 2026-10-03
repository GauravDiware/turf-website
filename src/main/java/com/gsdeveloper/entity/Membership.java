package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "memberships")
@Getter @Setter @NoArgsConstructor
public class Membership {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true) private String name;
    private String description;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(name = "duration_days", nullable = false) private Integer durationDays;
    @Column(nullable = false) private boolean active = true;
}
