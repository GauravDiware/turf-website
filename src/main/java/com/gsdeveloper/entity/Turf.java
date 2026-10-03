package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "turfs")
@Getter
@Setter
@NoArgsConstructor
public class Turf {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String location;

    private Double pricePerHour;

    private String sportType;

    @Column(length = 500)
    private String description;

    private boolean available = true;

}
