package com.yuvraj.maintenanceservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaintenanceResponseDto {
    private Long id;

    private Long vehicleId;

    private String description;

    private Double cost;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status;
}
