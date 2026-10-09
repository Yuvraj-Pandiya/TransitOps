package com.yuvraj.tripservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripCompletionRequestDto {
    private Double finalOdometer;
    private Double fuelConsumed;
}
