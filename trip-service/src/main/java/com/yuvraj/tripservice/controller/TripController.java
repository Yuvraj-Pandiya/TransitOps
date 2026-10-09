package com.yuvraj.tripservice.controller;

import com.yuvraj.tripservice.dto.TripCompletionRequestDto;
import com.yuvraj.tripservice.dto.TripRequestDto;
import com.yuvraj.tripservice.dto.TripResponseDto;
import com.yuvraj.tripservice.service.TripService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trips")
@AllArgsConstructor
public class TripController {

    private final TripService tripService;

    // Get Trip By Id
    @GetMapping("{id}")
    public ResponseEntity<TripResponseDto> getTripById(@PathVariable Long id){
        return tripService.getTripById(id);
    }

    // Create Trip i.e Draft (accept in TripRequestDto object )
    @PostMapping
    public ResponseEntity<TripResponseDto> createTrip(@RequestBody TripRequestDto tripRequestDto){
        return tripService.createTrip(tripRequestDto);
    }

    // Dispatch Lifecycle starts stage 1
    @PutMapping("dispatch/{id}")
    public  ResponseEntity<TripResponseDto> updateTripDispatch(@PathVariable Long id){
        return tripService.updateTripDispatch(id);
    }

    // Complete Lifecycle starts stage 2
    @PutMapping("complete/{id}")
    public ResponseEntity<TripResponseDto> updateTripComplete(@PathVariable Long id, @RequestBody TripCompletionRequestDto tripCompletionRequestDto) {
        return tripService.updateTripComplete(id,tripCompletionRequestDto);
    }

    // Cancel   Lifecycle starts stage 3
    @PutMapping("cancel/{id}")
    public ResponseEntity<TripResponseDto>  updateTripCancel(@PathVariable Long id) {
        return tripService.updateTripCancel(id);
    }

    //delete Trip
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteTrip(@PathVariable Long id){
        return tripService.deleteTrip(id);
    }
}
