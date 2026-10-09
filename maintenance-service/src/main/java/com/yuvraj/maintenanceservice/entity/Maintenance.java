package com.yuvraj.maintenanceservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "maintenances")
@Data
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vehicleId;

    private String description;

    private Double cost;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status;
}
