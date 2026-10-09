package com.yuvraj.driverservice.service;

import com.yuvraj.driverservice.dto.DriverDto;
import com.yuvraj.driverservice.entity.Driver;
import com.yuvraj.driverservice.repository.DriverRepository;
import com.yuvraj.driverservice.service.mapper.DriverMapper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    public ResponseEntity<String> createDriver(Driver driver) {
        try{
            driverRepository.save(driver);
            return new ResponseEntity<>("Driver created", HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Driver>> getAllDrivers() {
        try{
            return new ResponseEntity<>(driverRepository.findAll(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<DriverDto> getDriverById(Long id) {
        try{
            Optional<Driver> driver = driverRepository.findById(id);
            if(driver.isPresent()){
                return new ResponseEntity<>(
                        driverMapper.driverToDriverDto(driver.get()), HttpStatus.OK);
            }
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> updateDriver(Driver driver) {
        try{
            if(driverRepository.findById(driver.getId()).isPresent()){
                driverRepository.save(driver);
                return new ResponseEntity<>("Driver updated", HttpStatus.OK);
            }
            return new ResponseEntity<>("Driver not found", HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> deleteDriverById(Long id) {
        try{
            if(!driverRepository.existsById(id)){
                return new ResponseEntity<>("Vehicle Not Found",HttpStatus.NOT_FOUND);
            }
            driverRepository.deleteById(id);
            return new ResponseEntity<>("Vehicle deleted", HttpStatus.OK);
        }
        catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Cannot delete vehicle: It is referenced elsewhere.", HttpStatus.CONFLICT); // 409

        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    public ResponseEntity<String> updateStatus(Long id, String status) {
        try {
            Optional<Driver> driver = driverRepository.findById(id);
            if(driver.isPresent()){
                Driver driverToUpdate = driver.get();
                driverToUpdate.setStatus(status);
                driverRepository.save(driverToUpdate);
            }
            else return new ResponseEntity<>("Driver Not Found", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            System.out.println("Hello");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Driver updated", HttpStatus.OK);
    }
}
