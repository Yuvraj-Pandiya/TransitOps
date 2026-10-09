package com.yuvraj.vehicleservice.service.mapper;

import com.yuvraj.vehicleservice.dto.VehicleDto;
import com.yuvraj.vehicleservice.entity.Vehicle;
import org.springframework.stereotype.Component;


@Component
public class VehicleMapperImpl implements VehicleMapper {

    @Override
    public VehicleDto vehicleToVehicleDto(Vehicle vehicle) {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(vehicle.getId());
        vehicleDto.setName(vehicle.getName());
        vehicleDto.setRegistrationNumber(vehicle.getRegistrationNumber());
        vehicleDto.setType(vehicle.getType());
        vehicleDto.setMaxLoadCapacity(vehicle.getMaxLoadCapacity());
        vehicleDto.setOdometer(vehicle.getOdometer());
        vehicleDto.setAcquisitionCost(vehicle.getAcquisitionCost());
        vehicleDto.setStatus(vehicle.getStatus());
        vehicleDto.setRegion(vehicle.getRegion());
        return vehicleDto;
    }
}
