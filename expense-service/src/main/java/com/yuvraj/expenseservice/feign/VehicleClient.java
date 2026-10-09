package com.yuvraj.expenseservice.feign;

import com.yuvraj.expenseservice.dto.VehicleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("VEHICLE-SERVICE")
public interface VehicleClient {
    @GetMapping("vehicles/{id}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable("id") Long id);

}
