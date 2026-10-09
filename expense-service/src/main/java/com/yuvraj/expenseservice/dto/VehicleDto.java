package com.yuvraj.expenseservice.dto;

import lombok.Data;

@Data
public class VehicleDto {
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
