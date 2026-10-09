package com.yuvraj.driverservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity(name = "drivers")
@Data
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
