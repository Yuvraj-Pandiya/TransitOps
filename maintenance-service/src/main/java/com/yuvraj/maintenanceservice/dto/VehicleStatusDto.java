package com.yuvraj.maintenanceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleStatusDto {

    private Long id;
    private String status;
    private Double finalOdometer;

}
