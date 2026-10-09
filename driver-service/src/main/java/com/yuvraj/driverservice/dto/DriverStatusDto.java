package com.yuvraj.driverservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverStatusDto {

    private Long id;
    private String status;
}
