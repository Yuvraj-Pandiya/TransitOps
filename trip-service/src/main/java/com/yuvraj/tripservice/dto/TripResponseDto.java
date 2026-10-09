package com.yuvraj.tripservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripResponseDto {

    private Long id;
    private String source;
    private String destination;
    private Long vehicleId;
    private Long driverId;
    private Double cargoWeight;
    private Double plannedDistance;
    //    Total earnings or cost charged for this trip (e.g., 12500.00).
    private Double revenue;
    //    Lifecycle state: Draft, Dispatched, Completed, or Cancelled.
    private String status;
    private LocalDateTime dispatchDate;
    private LocalDateTime completionDate;
    private Double finalOdometer;
    private Double fuelConsumed;
}
