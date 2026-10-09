package com.yuvraj.driverservice.controller;

import com.yuvraj.driverservice.dto.DriverDto;
import com.yuvraj.driverservice.dto.DriverStatusDto;
import com.yuvraj.driverservice.entity.Driver;
import com.yuvraj.driverservice.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("drivers")
@AllArgsConstructor
public class DriverController {

    private final DriverService driverService;

    //add driver
    @PostMapping
    public ResponseEntity<String> createDriver(@RequestBody Driver driver) {
        return driverService.createDriver(driver);
    }

    //Get All Drivers
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    //Get Driver By Id
    @GetMapping("{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    //Update Driver
    @PutMapping
    public ResponseEntity<String> updateDriver(@RequestBody Driver driver) {
        return driverService.updateDriver(driver);
    }

    //delete Driver
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteDriverById(@PathVariable Long id) {
        return driverService.deleteDriverById(id);
    }

    @PutMapping("status")
    public ResponseEntity<String> updateStatus(@RequestBody DriverStatusDto driverStatusDto) {
        return driverService.updateStatus(driverStatusDto.getId(),driverStatusDto.getStatus());
    }
}
