package com.yuvraj.expenseservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vehicleId;

    private Long tripId;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    private Double liters;

    private Double amount;

    private LocalDate expenseDate;


}
