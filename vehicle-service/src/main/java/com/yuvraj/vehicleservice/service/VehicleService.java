package com.yuvraj.vehicleservice.service;

import com.yuvraj.vehicleservice.dto.VehicleDto;
import com.yuvraj.vehicleservice.entity.Vehicle;
import com.yuvraj.vehicleservice.repository.VehicleRepository;
import com.yuvraj.vehicleservice.service.mapper.VehicleMapper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public ResponseEntity<String> createVehicle(Vehicle vehicle) {
        try {
            vehicleRepository.save(vehicle);
        }catch (Exception e) {
            return new  ResponseEntity<>("User Input Error ",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Vehicle created", HttpStatus.CREATED);
    }

//    public ResponseEntity<Vehicle> getVehicleById(Long id) {
//        try{
//            Optional<Vehicle> vehicle = vehicleRepository.findById(id);
//            if(vehicle.isPresent()){
//                return new ResponseEntity<>(vehicle.get(), HttpStatus.OK);
//            }
//        }
//        catch (Exception e){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

    public ResponseEntity<String> updateVehicle(Vehicle vehicle) {
        try {
            Long id =  vehicle.getId();
            if(vehicleRepository.existsById(id)){
                vehicleRepository.save(vehicle);
            }
            else  {
                return new ResponseEntity<>("Vehicle Not Exists", HttpStatus.NOT_FOUND);
            }
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Vehicle updated", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteVehicle(Long id) {
        try{
            if(!vehicleRepository.existsById(id)){
                return new ResponseEntity<>("Vehicle Not Found",HttpStatus.NOT_FOUND);
            }
            vehicleRepository.deleteById(id);
            return new ResponseEntity<>("Vehicle deleted", HttpStatus.OK);
        }
        catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Cannot delete vehicle: It is referenced elsewhere.", HttpStatus.CONFLICT); // 409

        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    public ResponseEntity<VehicleDto> getVehicleById(Long id) {
         try{
             Optional<Vehicle> vehicle = vehicleRepository.findById(id);
             if(vehicle.isPresent()){
                 return new ResponseEntity<VehicleDto>(
                         vehicleMapper.vehicleToVehicleDto(vehicle.get()),
                         HttpStatus.OK);
             }
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
         }catch (Exception e){
             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
         }
    }

    public ResponseEntity<String> updateStatus(Long id, String status,Double finalOdometer) {
        try{
            Vehicle vehicle = vehicleRepository.findById(id).orElse(null);

            if (vehicle == null) {
                return new ResponseEntity<>("Vehicle Not Found", HttpStatus.NOT_FOUND);
            }

            vehicle.setStatus(status);

            if (finalOdometer != null) {
                // BigDecimal comparison: check if finalOdometer is less than current odometer
                if (finalOdometer.compareTo(vehicle.getOdometer()) < 0) {
                    return new ResponseEntity<>("Odometer is Incorrect (Cannot be less than current reading)", HttpStatus.BAD_REQUEST);
                }
                vehicle.setOdometer(finalOdometer);
            }

            vehicleRepository.save(vehicle);
            return new ResponseEntity<>("Vehicle Status Updated", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("User Input Error ",HttpStatus.BAD_REQUEST);
        }
    }
}
