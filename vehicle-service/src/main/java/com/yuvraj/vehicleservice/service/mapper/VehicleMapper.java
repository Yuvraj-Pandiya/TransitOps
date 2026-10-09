package com.yuvraj.vehicleservice.service.mapper;

import com.yuvraj.vehicleservice.dto.VehicleDto;
import com.yuvraj.vehicleservice.entity.Vehicle;
import org.springframework.stereotype.Component;

//@Component
public interface VehicleMapper {
    public VehicleDto vehicleToVehicleDto(Vehicle vehicle);
}
