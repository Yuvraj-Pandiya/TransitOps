package com.yuvraj.expenseservice.service.mapper;

import com.yuvraj.expenseservice.dto.ExpenseRequestDto;
import com.yuvraj.expenseservice.dto.ExpenseResponseDto;
import com.yuvraj.expenseservice.entity.Expense;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ExpenseMapperImpl implements ExpenseMapper {

    @Override
    public Expense expenseRequestDtoToExpense(ExpenseRequestDto expenseRequestDto) {
        Expense expense = new Expense();
        expense.setVehicleId(expenseRequestDto.getVehicleId());
        expense.setTripId(expenseRequestDto.getTripId());
        expense.setExpenseType(expenseRequestDto.getType());
        expense.setLiters(expenseRequestDto.getLiters());
        expense.setExpenseDate(LocalDate.now());
        expense.setAmount(expenseRequestDto.getAmount());
        return expense;
    }

    @Override
    public ExpenseResponseDto expenseToExpenseResponseDto(Expense expense) {
        ExpenseResponseDto expenseResponseDto = new ExpenseResponseDto();
        expenseResponseDto.setId(expense.getId());
        expenseResponseDto.setVehicleId(expense.getVehicleId());
        expenseResponseDto.setTripId(expense.getTripId());
        expenseResponseDto.setExpenseType(expense.getExpenseType());
        expenseResponseDto.setLiters(expense.getLiters());
        expenseResponseDto.setAmount(expense.getAmount());
        expenseResponseDto.setExpenseDate(expense.getExpenseDate());
        return expenseResponseDto;
    }
}
