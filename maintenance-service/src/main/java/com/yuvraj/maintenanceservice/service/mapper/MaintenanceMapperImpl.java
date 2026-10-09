package com.yuvraj.maintenanceservice.service.mapper;

import com.yuvraj.maintenanceservice.dto.MaintenanceRequestDto;
import com.yuvraj.maintenanceservice.dto.MaintenanceResponseDto;
import com.yuvraj.maintenanceservice.entity.Maintenance;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MaintenanceMapperImpl implements MaintenanceMapper {

    @Override
    public Maintenance maintenanceToMaintenanceRequestDto(MaintenanceRequestDto maintenanceRequestDto) {
        Maintenance maintenance = new Maintenance();
        maintenance.setVehicleId(maintenanceRequestDto.getVehicleId());
        maintenance.setDescription(maintenanceRequestDto.getDescription());
        maintenance.setCost(maintenanceRequestDto.getCost());
        maintenance.setStartDate(LocalDateTime.now());
        maintenance.setStatus("Active");
        return maintenance;
    }

    public MaintenanceResponseDto  maintenanceToMaintenanceResponseDto(Maintenance maintenance) {
        MaintenanceResponseDto maintenanceResponseDto = new MaintenanceResponseDto();
        maintenanceResponseDto.setVehicleId(maintenance.getVehicleId());
        maintenanceResponseDto.setDescription(maintenance.getDescription());
        maintenanceResponseDto.setCost(maintenance.getCost());
        maintenanceResponseDto.setStartDate(LocalDateTime.now());
        maintenanceResponseDto.setStatus("Active");
        maintenanceResponseDto.setEndDate(maintenance.getEndDate());
        maintenanceResponseDto.setId(maintenance.getId());
        return maintenanceResponseDto;
    }
}
