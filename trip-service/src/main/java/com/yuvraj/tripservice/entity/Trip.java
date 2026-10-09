package com.yuvraj.tripservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "trips")
@Data
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
