package com.yuvraj.expenseservice.dto;

import com.yuvraj.expenseservice.entity.ExpenseType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseResponseDto {

    private Long id;

    private Long vehicleId;

    private Long tripId;

    private ExpenseType expenseType;

    private Double liters;

    private Double amount;

    private LocalDate expenseDate;
}
