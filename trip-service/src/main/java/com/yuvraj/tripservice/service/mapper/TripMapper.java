package com.yuvraj.tripservice.service.mapper;

import com.yuvraj.tripservice.entity.Trip;
import com.yuvraj.tripservice.dto.TripRequestDto;
import com.yuvraj.tripservice.dto.TripResponseDto;

public interface TripMapper {

    public TripResponseDto tripToTripDto(Trip trip);
    public Trip tripDtoToTrip(TripRequestDto tripRequestDto);

}
