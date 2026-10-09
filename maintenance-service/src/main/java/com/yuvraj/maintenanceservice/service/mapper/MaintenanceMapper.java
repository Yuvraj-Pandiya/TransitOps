package com.yuvraj.maintenanceservice.service.mapper;

import com.yuvraj.maintenanceservice.dto.MaintenanceRequestDto;
import com.yuvraj.maintenanceservice.dto.MaintenanceResponseDto;
import com.yuvraj.maintenanceservice.entity.Maintenance;

public interface MaintenanceMapper {
    public Maintenance maintenanceToMaintenanceRequestDto(MaintenanceRequestDto maintenanceRequestDto);

    public MaintenanceResponseDto maintenanceToMaintenanceResponseDto(Maintenance maintenance);
}
