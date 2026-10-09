package com.yuvraj.maintenanceservice.controller;

import com.yuvraj.maintenanceservice.dto.MaintenanceRequestDto;
import com.yuvraj.maintenanceservice.dto.MaintenanceResponseDto;
import com.yuvraj.maintenanceservice.dto.MaintenanceUpdateDto;
import com.yuvraj.maintenanceservice.entity.Maintenance;
import com.yuvraj.maintenanceservice.service.MaintenanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("maintenances")
@AllArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<String> addMaintenance(@RequestBody MaintenanceRequestDto maintenanceRequestDto) {
        return maintenanceService.addMaintenance(maintenanceRequestDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<MaintenanceResponseDto> getMaintenance(@PathVariable Long id) {
        return maintenanceService.getMaintenance(id);
    }

    // get all record by vehicle id
    @GetMapping("vehicle/{id}")
    public ResponseEntity<List<MaintenanceResponseDto>> getMaintenanceByVehicleId(@PathVariable Long id) {
        return maintenanceService.getMaintenanceByVehicleId(id);
    }


    //Close Maintenance
    @PutMapping("close/{id}")
    public ResponseEntity<String> closeMaintenance(@PathVariable Long id) {
        return maintenanceService.closeMaintenance(id);
    }

    @PutMapping
    public ResponseEntity<String> updateMaintenance(@RequestBody MaintenanceUpdateDto maintenanceUpdateDto) {
        return maintenanceService.updateMaintenance(maintenanceUpdateDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteMaintenance(@PathVariable("id") Long id) {
        return maintenanceService.deleteMaintenance(id);
    }

}
