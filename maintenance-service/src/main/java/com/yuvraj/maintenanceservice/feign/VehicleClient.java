package com.yuvraj.maintenanceservice.feign;

import com.yuvraj.maintenanceservice.dto.VehicleDto;
import com.yuvraj.maintenanceservice.dto.VehicleStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("VEHICLE-SERVICE")
public interface VehicleClient {

    @GetMapping("vehicles/{id}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable("id") Long id);

    @PutMapping("vehicles/status")
    public ResponseEntity<String> updateStatus(@RequestBody VehicleStatusDto vehicleStatusDto);
}
