package com.yuvraj.vehicleservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "vehicles")
@Data
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String registrationNumber;
    private String name;
    private String type;
    private Double maxLoadCapacity;
    private Double odometer;
    //the total amount a business spends to buy a new asset
    private Double acquisitionCost;
    private String status;
    private String region;

}
