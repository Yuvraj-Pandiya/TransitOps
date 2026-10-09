package com.yuvraj.driverservice.service.mapper;

import com.yuvraj.driverservice.dto.DriverDto;
import com.yuvraj.driverservice.entity.Driver;
import org.springframework.stereotype.Component;

//@Component
public interface DriverMapper {
    public DriverDto driverToDriverDto(Driver driver);
}
