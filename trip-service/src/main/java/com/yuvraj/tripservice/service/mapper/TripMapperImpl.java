package com.yuvraj.tripservice.service.mapper;

import com.yuvraj.tripservice.dto.TripRequestDto;
import com.yuvraj.tripservice.dto.TripResponseDto;
import com.yuvraj.tripservice.entity.Trip;
import org.springframework.stereotype.Component;

@Component
public class TripMapperImpl implements TripMapper {

    @Override
    public TripResponseDto tripToTripDto(Trip trip) {
        if (trip == null) {
            return null;
        }

        TripResponseDto dto = new TripResponseDto();
        dto.setId(trip.getId());
        dto.setSource(trip.getSource());
        dto.setDestination(trip.getDestination());
        dto.setVehicleId(trip.getVehicleId());
        dto.setDriverId(trip.getDriverId());
        dto.setCargoWeight(trip.getCargoWeight());
        dto.setPlannedDistance(trip.getPlannedDistance());
        dto.setRevenue(trip.getRevenue());
        dto.setStatus(trip.getStatus());
        dto.setDispatchDate(trip.getDispatchDate());
        dto.setCompletionDate(trip.getCompletionDate());
        dto.setFinalOdometer(trip.getFinalOdometer());
        dto.setFuelConsumed(trip.getFuelConsumed());

        return dto;
    }

    @Override
    public Trip tripDtoToTrip(TripRequestDto tripRequestDto) {
        if (tripRequestDto == null) {
            return null;
        }

        Trip trip = new Trip();
        trip.setSource(tripRequestDto.getSource());
        trip.setDestination(tripRequestDto.getDestination());
        trip.setVehicleId(tripRequestDto.getVehicleId());
        trip.setDriverId(tripRequestDto.getDriverId());
        trip.setCargoWeight(tripRequestDto.getCargoWeight());
        trip.setPlannedDistance(tripRequestDto.getPlannedDistance());
        trip.setRevenue(tripRequestDto.getRevenue());

        // Every new trip starts in 'Draft' status by default
        trip.setStatus("Draft");

        return trip;
    }
}