package com.yuvraj.tripservice.service;

import com.yuvraj.tripservice.dto.*;
import com.yuvraj.tripservice.entity.Trip;
import com.yuvraj.tripservice.feign.DriverClient;
import com.yuvraj.tripservice.feign.VehicleClient;
import com.yuvraj.tripservice.repository.TripRepository;
import com.yuvraj.tripservice.service.mapper.TripMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Autowired
    VehicleClient vehicleClient;

    @Autowired
    DriverClient driverClient;

    public ResponseEntity<TripResponseDto> getTripById(Long id) {
        try{
            if(!tripRepository.existsById(id)){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Trip trip = tripRepository.findById(id).get();
            TripResponseDto tripResponseDto = tripMapper.tripToTripDto(trip);
            return new ResponseEntity<>(tripResponseDto, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<TripResponseDto> createTrip(TripRequestDto tripRequestDto) {
        try{
            Trip trip = tripMapper.tripDtoToTrip(tripRequestDto);
            trip = tripRepository.save(trip);
            return new ResponseEntity<>(tripMapper.tripToTripDto(trip),HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<TripResponseDto> updateTripDispatch(Long id) {
        try{

            Optional<Trip> tripOptional = tripRepository.findById(id);
            if(!tripOptional.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Trip trip = tripOptional.get();
            if(!trip.getStatus().equalsIgnoreCase("Draft")){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
//            return new ResponseEntity<>(tripMapper.tripToTripDto(trip), HttpStatus.OK);
            // 1. Vehicle Validation (via OpenFeign call to vehicle-service):
            // Does the vehicle (vehicleId) exist?
            // Is its status "Available"?
            // Is cargoWeight <= the vehicle's maxLoadCapacity?

            ResponseEntity<VehicleDto> responseVehicle = vehicleClient.getVehicleById(
                    trip.getVehicleId());
            VehicleDto vehicleDto = responseVehicle.getBody();

            ResponseEntity<DriverDto> responseDriver = driverClient.getDriverById(
                    trip.getDriverId()
            );
            DriverDto driverDto = responseDriver.getBody();

            if(!"Available".equalsIgnoreCase(vehicleDto.getStatus()) ||
                    vehicleDto.getMaxLoadCapacity()<trip.getCargoWeight() ||
                    !"Available".equalsIgnoreCase(driverDto.getStatus())
            ){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // 2. Driver Validation (does driverId exist, is their status available)

            // If all check passed
            trip.setStatus("Dispatched");
            trip.setDispatchDate(LocalDateTime.now());
            tripRepository.save(trip);

            // Now vehicle status got updated
            // Now driver status got updated
            vehicleClient.updateStatus(new VehicleStatusDto(trip.getVehicleId(),"On Trip",null));
            driverClient.updateStatus(new DriverStatusDto(trip.getDriverId(),"On Trip"));
            return new ResponseEntity<>(tripMapper.tripToTripDto(trip),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<TripResponseDto> updateTripComplete(Long id, TripCompletionRequestDto tripCompletionRequestDto) {
        try{
            Optional<Trip> optionalTrip = tripRepository.findById(id);

            if(!optionalTrip.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Trip trip = optionalTrip.get();

            if(!trip.getStatus().equalsIgnoreCase("Dispatched")){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            trip.setStatus("Completed");
            trip.setCompletionDate(LocalDateTime.now());
            trip.setFinalOdometer(tripCompletionRequestDto.getFinalOdometer());
            trip.setFuelConsumed(tripCompletionRequestDto.getFuelConsumed());

            tripRepository.save(trip);

            // vehicle-service and driver-service update
            vehicleClient.updateStatus(new VehicleStatusDto(trip.getVehicleId(),"Available",tripCompletionRequestDto.getFinalOdometer()));
            driverClient.updateStatus(new DriverStatusDto(trip.getDriverId(),"Available"));

            // Fuel consumed is sended to expense-service to track of trip cost and fuel
            return new ResponseEntity<>(tripMapper.tripToTripDto(trip),HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<TripResponseDto> updateTripCancel(Long id) {
        try{
            Optional<Trip> tripOptional = tripRepository.findById(id);
            if(!tripOptional.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Trip trip = tripOptional.get();

            if(trip.getStatus().equalsIgnoreCase("Completed")
            || trip.getStatus().equalsIgnoreCase("Cancelled")){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if(trip.getStatus().equalsIgnoreCase("Dispatched")){
                vehicleClient.updateStatus(new VehicleStatusDto(trip.getVehicleId(),"Available",null));
                driverClient.updateStatus(new DriverStatusDto(trip.getDriverId(),"Available"));
            }

            trip.setStatus("Cancelled");
            Trip updatedTrip = tripRepository.save(trip);
            TripResponseDto tripResponseDto = tripMapper.tripToTripDto(updatedTrip);

            return new ResponseEntity<>(tripResponseDto, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<String> deleteTrip(Long id) {
        try{
            Optional<Trip> tripOptional = tripRepository.findById(id);
            if(!tripOptional.isPresent()){
                return new ResponseEntity<>("Trip you want to delete is not available!!",HttpStatus.NOT_FOUND);
            }

            Trip trip = tripOptional.get();

            if(trip.getStatus().equalsIgnoreCase("Dispatched")){
                return new ResponseEntity<>("Cannot delete an active, dispatched trip. Cancel or complete it first.",HttpStatus.CONFLICT);
            }

            tripRepository.delete(trip);
            return new ResponseEntity<>("Trip has been deleted",HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
