package com.yuvraj.maintenanceservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class MaintenanceRequestDto {
    private Long vehicleId;
    private String description;
    private Double cost;
}
