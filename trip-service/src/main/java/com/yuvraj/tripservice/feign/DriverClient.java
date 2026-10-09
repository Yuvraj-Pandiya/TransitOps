package com.yuvraj.tripservice.feign;

import com.yuvraj.tripservice.dto.DriverDto;
import com.yuvraj.tripservice.dto.DriverStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient("DRIVER-SERVICE")
public interface DriverClient {

    @GetMapping("drivers/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable("id") Long id);

    @PutMapping("drivers/status")
    public ResponseEntity<String> updateStatus(@RequestBody DriverStatusDto driverStatusDto);

    }
