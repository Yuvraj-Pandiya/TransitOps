package com.yuvraj.maintenanceservice.dto;

import lombok.Data;

@Data
public class MaintenanceUpdateDto {
    private Long id;
    private String description;
    private Double cost;
}
