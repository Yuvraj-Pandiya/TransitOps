package com.yuvraj.maintenanceservice.service;

import com.yuvraj.maintenanceservice.dto.*;
import com.yuvraj.maintenanceservice.entity.Maintenance;
import com.yuvraj.maintenanceservice.feign.VehicleClient;
import com.yuvraj.maintenanceservice.repository.MaintenanceRepository;
import com.yuvraj.maintenanceservice.service.mapper.MaintenanceMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceMapper maintenanceMapper;

    @Autowired
    private VehicleClient vehicleClient;

    public ResponseEntity<String> addMaintenance(MaintenanceRequestDto maintenanceRequestDto) {
        try{
            Maintenance maintenance = maintenanceMapper.maintenanceToMaintenanceRequestDto(maintenanceRequestDto);
            ResponseEntity<VehicleDto> vehicleDtoResponseEntity = vehicleClient.getVehicleById(maintenance.getVehicleId());
            if (vehicleDtoResponseEntity.getStatusCode() == HttpStatus.OK) {
                VehicleDto vehicleDto = vehicleDtoResponseEntity.getBody();
                String status =  vehicleDto.getStatus();

                if(status.equalsIgnoreCase("On Trip") ||
                        status.equalsIgnoreCase("Retired")){
                    return new ResponseEntity<>("Vehicle is currently not available!!",HttpStatus.NOT_FOUND);
                }
                if(status.equalsIgnoreCase("In Shop")){
                    return new ResponseEntity<>("Vehicle is already IN_SHOP undergoing maintenance. Close the current maintenance record first.",HttpStatus.NOT_FOUND);
                }

                if(status.equalsIgnoreCase("Available")){
                    VehicleStatusDto vehicleStatusDto = new VehicleStatusDto(
                            vehicleDto.getId(),"In Shop",null
                    );
                    vehicleClient.updateStatus(vehicleStatusDto);
                    maintenanceRepository.save(maintenance);
                    return new ResponseEntity<>("Vehicle is available",HttpStatus.ACCEPTED);
                }
            }
            else{
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>("Maintenance added successfully", HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<MaintenanceResponseDto> getMaintenance(Long id) {
        try{
            Optional<Maintenance> maintenance = maintenanceRepository.findById(id);
            if(!maintenance.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            MaintenanceResponseDto maintenanceResponseDto = maintenanceMapper.maintenanceToMaintenanceResponseDto(maintenance.get());
            return new ResponseEntity<>(maintenanceResponseDto, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<MaintenanceResponseDto>> getMaintenanceByVehicleId(Long id) {
        try{
            Optional<List<Maintenance>> maintenance = maintenanceRepository
                    .findMaintenanceByVehicleId(id);
            if(!maintenance.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<Maintenance> list =  maintenance.get();
            List<MaintenanceResponseDto> listResponseDto = new ArrayList<>();
            for(Maintenance maintenanceDto : list)
            {
                listResponseDto.add(maintenanceMapper.maintenanceToMaintenanceResponseDto(maintenanceDto));
            }
            return new ResponseEntity<>(listResponseDto,HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> closeMaintenance(Long id) {
        try{
            Optional<Maintenance> maintenance = maintenanceRepository.findById(id);
            if(!maintenance.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Maintenance maintenanceDto = maintenance.get();

            if(maintenanceDto.getStatus().equalsIgnoreCase("Active")){
                vehicleClient.updateStatus(new VehicleStatusDto(maintenanceDto.getVehicleId(),
                        "Available",null));
                maintenanceDto.setStatus("Closed");
                maintenanceDto.setEndDate(LocalDateTime.now());
                maintenanceRepository.save(maintenanceDto);
            }
            else return new ResponseEntity<>("Maintenance is currently not available!!",HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Maintenance closed successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> updateMaintenance(MaintenanceUpdateDto maintenanceUpdateDto) {
        try{
            Optional<Maintenance> maintenance = maintenanceRepository.findById(maintenanceUpdateDto.getId());
            if(!maintenance.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Maintenance maintenanceDto = maintenance.get();
            maintenanceDto.setDescription(maintenanceUpdateDto.getDescription());
            maintenanceDto.setCost(maintenanceUpdateDto.getCost());
            maintenanceRepository.save(maintenanceDto);
            return new ResponseEntity<>("Maintenance updated successfully", HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteMaintenance(Long id) {
        try{
            Optional<Maintenance> maintenance = maintenanceRepository.findById(id);
            if(!maintenance.isPresent()){
                return new ResponseEntity<>("No such maintenance found by this id",HttpStatus.NOT_FOUND);
            }
            if(maintenance.get().getStatus().equalsIgnoreCase("Closed")){
                maintenanceRepository.deleteById(id);
                return new ResponseEntity<>("Maintenance deleted successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("Maintenance is currently in Active!! first close it!!",HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
