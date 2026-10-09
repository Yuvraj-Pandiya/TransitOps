package com.yuvraj.expenseservice.dto;

import com.yuvraj.expenseservice.entity.ExpenseType;
import lombok.Data;

@Data
public class ExpenseRequestDto {
    private Long vehicleId;
    private Long tripId;
    private ExpenseType type;
    private Double amount;
    private Double liters;
}
