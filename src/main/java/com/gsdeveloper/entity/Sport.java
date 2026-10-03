package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity @Table(name = "sports") @Getter @Setter @NoArgsConstructor
public class Sport {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true) private String name;
    private String description;
    private String iconUrl;
    @Column(nullable = false) private boolean active = true;
}
