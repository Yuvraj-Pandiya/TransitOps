package com.yuvraj.expenseservice.service.mapper;

import com.yuvraj.expenseservice.dto.ExpenseRequestDto;
import com.yuvraj.expenseservice.dto.ExpenseResponseDto;
import com.yuvraj.expenseservice.entity.Expense;

public interface ExpenseMapper {
    public Expense expenseRequestDtoToExpense(ExpenseRequestDto expenseRequestDto);
    public ExpenseResponseDto expenseToExpenseResponseDto(Expense expense);
}
