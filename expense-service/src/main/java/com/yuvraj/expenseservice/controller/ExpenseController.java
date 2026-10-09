package com.yuvraj.expenseservice.controller;

import com.yuvraj.expenseservice.dto.ExpenseRequestDto;
import com.yuvraj.expenseservice.dto.ExpenseResponseDto;
import com.yuvraj.expenseservice.service.ExpenseService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("expenses")
@AllArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<String> createExpense(@RequestBody ExpenseRequestDto expenseRequestDto) {
        return expenseService.createExpense(expenseRequestDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<ExpenseResponseDto> getExpenseById(@PathVariable("id") Long id){
        return expenseService.getExpenseById(id);
    }

    @GetMapping("vehicle/{id}")
    public List<ExpenseResponseDto> getExpenseByVehicleId(@PathVariable("id") Long id){
        return expenseService.getExpenseByVehicleId(id);
    }

    @GetMapping("type/{type}")
    public List<ExpenseResponseDto> getExpenseByType(@PathVariable("type") String type){
        return expenseService.getExpenseByType(type);
    }

    @GetMapping("total/vehicle/{id}")
    public ResponseEntity<Integer> getTotalExpenseByVehicleId(@PathVariable("id") Long id){
        return expenseService.getTotalExpenseByVehicleId(id);
    }

//    @PutMapping("{id}")
//    public ResponseEntity<String> updateExpense(@PathVariable("id") Long id,@RequestBody ExpenseRequestDto expenseRequestDto) {
//        return expenseService.updateExpense(id,expenseRequestDto);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpenseById(@PathVariable("id") Long id){
        return expenseService.deleteExpenseById(id);
    }

}