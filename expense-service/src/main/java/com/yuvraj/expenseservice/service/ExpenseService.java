package com.yuvraj.expenseservice.service;

import com.yuvraj.expenseservice.dto.ExpenseRequestDto;
import com.yuvraj.expenseservice.dto.ExpenseResponseDto;
import com.yuvraj.expenseservice.dto.VehicleDto;
import com.yuvraj.expenseservice.entity.Expense;
import com.yuvraj.expenseservice.entity.ExpenseType;
import com.yuvraj.expenseservice.feign.VehicleClient;
import com.yuvraj.expenseservice.repository.ExpenseRepository;
import com.yuvraj.expenseservice.service.mapper.ExpenseMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.cfg.MapperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ExpenseService {

    private final ExpenseRepository  expenseRepository;

    private final ExpenseMapper expenseMapper;

    @Autowired
    VehicleClient vehicleClient;
    @Autowired
    private MapperBuilder mapperBuilder;

    public ResponseEntity<String> createExpense(ExpenseRequestDto expenseRequestDto) {
        // Vehicle Existance Via feign
        try {
            VehicleDto vehicleDto = vehicleClient.getVehicleById(expenseRequestDto.getVehicleId()).getBody();
            if(vehicleDto==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle Not Found with this id"+expenseRequestDto.getVehicleId());
            }

        }catch (feign.FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle Not Found with this id"+expenseRequestDto.getVehicleId());
        }

        // Vehicle Amount
        if(expenseRequestDto.getAmount()==null || expenseRequestDto.getAmount()<=0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount of expense must be greater than 0");
        }

        if(expenseRequestDto.getType()==ExpenseType.FUEL){
            if(expenseRequestDto.getLiters()==null || expenseRequestDto.getLiters()<=0){
                return new ResponseEntity<>("Liters of expense must be greater than 0", HttpStatus.BAD_REQUEST);
            }
        }
        else{
            expenseRequestDto.setLiters(null);
        }

        Expense expense = expenseMapper.expenseRequestDtoToExpense(expenseRequestDto);
        Expense savedExpense = expenseRepository.save(expense);

        return new ResponseEntity<>("expense saved successfully", HttpStatus.OK);
    }

    public ResponseEntity<ExpenseResponseDto> getExpenseById(Long id) {
        try{
            Optional<Expense> expense = expenseRepository.findById(id);
            if(!expense.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            ExpenseResponseDto expenseResponseDto = expenseMapper.expenseToExpenseResponseDto(expense.get());
            return new ResponseEntity<>(expenseResponseDto, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExpenseResponseDto());
        }
    }

    public List<ExpenseResponseDto> getExpenseByVehicleId(Long id) {
        List<ExpenseResponseDto> expenseResponseDtos = new ArrayList<>();

        List<Expense> expenseList = expenseRepository.getExpensesByVehicleId(id);

        for(Expense expense : expenseList)
        {
            expenseResponseDtos.add(expenseMapper.expenseToExpenseResponseDto(expense));
        }

        return expenseResponseDtos;
    }

    public List<ExpenseResponseDto> getExpenseByType(String type) {
        List<ExpenseResponseDto> dtoList =  new ArrayList<>();
        ExpenseType expenseType;
        try {
            expenseType = ExpenseType.valueOf(type.trim().toUpperCase());
        }catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
        List<Expense> expenseList = expenseRepository.getExpensesByExpenseType(expenseType);

        for(Expense expense : expenseList)
        {
            dtoList.add(expenseMapper.expenseToExpenseResponseDto(expense));
        }

        return dtoList;
    }

    public ResponseEntity<Integer> getTotalExpenseByVehicleId(Long id) {
        Integer totalExpense = 0;
        totalExpense = expenseRepository.getTotalExpenseByVehicleId(id);
        return new ResponseEntity<>(totalExpense, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteExpenseById(Long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if(!expense.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        expenseRepository.deleteById(id);
        return new ResponseEntity<>("expense deleted successfully", HttpStatus.OK);
    }

//    public ResponseEntity<String> updateExpense(Long id,ExpenseRequestDto expenseRequestDto) {
//        try {
//            Optional<Expense> optionalExpense = expenseRepository.findById(id);
//            if (!optionalExpense.isPresent()) {
//                return new ResponseEntity<>("This expense is not valid! Enter correct expense id", HttpStatus.NOT_FOUND);
//            }
//
//            Long vehicleId = expenseRequestDto.getVehicleId();
//            Long vehicleId1 = optionalExpense.get().getVehicleId();
//
//            Long tripId = expenseRequestDto.getTripId();
//            Long tripId1 = optionalExpense.get().getTripId();
//            if((!vehicleId.equals(vehicleId1)) || !tripId.equals(tripId1) ){
//                return new ResponseEntity<>("Vehicle or Trip not associated with this expense!!", HttpStatus.BAD_REQUEST);
//            }
//
//            if(expenseRequestDto.getType()==ExpenseType.FUEL){
//
//            }
//            expenseRepository.updateExpense(id,);
//
//            return new ResponseEntity<>("Expense updated successfully", HttpStatus.OK);
//        }
//        catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
