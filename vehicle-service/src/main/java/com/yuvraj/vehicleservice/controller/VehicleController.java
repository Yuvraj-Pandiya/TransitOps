package com.yuvraj.vehicleservice.controller;

import com.yuvraj.vehicleservice.dto.VehicleDto;
import com.yuvraj.vehicleservice.dto.VehicleStatusDto;
import com.yuvraj.vehicleservice.entity.Vehicle;
import com.yuvraj.vehicleservice.service.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("vehicles")
@AllArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;


    // method for adding vehicles in db
    @PostMapping
    public ResponseEntity<String> createVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.createVehicle(vehicle);
    }

//    //Get Vehicles by id
//    @GetMapping("{id}")
//    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id){
//        return vehicleService.getVehicleById(id);
//    }

    // Updating Vehicles
    @PutMapping
    public ResponseEntity<String> updateVehicle(@RequestBody Vehicle vehicle){
        return vehicleService.updateVehicle(vehicle);
    }

    //Deleting Vehicles
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Long id){
        return vehicleService.deleteVehicle(id);
    }


    // Now in trip we want status of vehhicle
    @GetMapping("{id}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id){
        return vehicleService.getVehicleById(id);
    }

    @PutMapping("status")
    public ResponseEntity<String> updateStatus(@RequestBody VehicleStatusDto  vehicleStatusDto){
        return vehicleService.updateStatus(vehicleStatusDto.getId(),vehicleStatusDto.getStatus(),vehicleStatusDto.getFinalOdometer());
    }
}
