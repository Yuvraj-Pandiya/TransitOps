package com.yuvraj.driverservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DriverDto {
    private Long id;

    private String name;
    //    private String address
    private String licenseNumber;
    private String licenseCategory;
    private LocalDate licenseExpiryDate;
    private String contactNumber;
    //    Saftey Score out of 100 from previous drives
    private Double safetyScore;
    private String status;
}
