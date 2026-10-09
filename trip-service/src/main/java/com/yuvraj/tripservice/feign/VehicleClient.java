package com.yuvraj.tripservice.feign;

import com.yuvraj.tripservice.dto.VehicleDto;
import com.yuvraj.tripservice.dto.VehicleStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient("VEHICLE-SERVICE")
public interface VehicleClient {

    @GetMapping("vehicles/{id}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable("id") Long id);

    @PutMapping("vehicles/status")
    public ResponseEntity<String> updateStatus(@RequestBody VehicleStatusDto vehicleStatusDto);
}
