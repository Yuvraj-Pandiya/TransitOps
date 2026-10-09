package com.yuvraj.tripservice.dto;

import lombok.Data;

@Data
public class TripRequestDto {
    private String source;
    private String destination;
    private Long vehicleId;
    private Long driverId;
    private Double cargoWeight;
    private Double plannedDistance;
    private Double revenue;
}
