package com.yuvraj.expenseservice.repository;

import com.yuvraj.expenseservice.entity.Expense;
import com.yuvraj.expenseservice.entity.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> getExpensesByVehicleId(Long vehicleId);

    List<Expense> getExpensesByExpenseType(ExpenseType expenseType);

    @Query("""
 SELECT SUM(exp.amount) FROM expenses As exp WHERE 
  exp.vehicleId =:id
   """)
    Integer getTotalExpenseByVehicleId(@Param("id")Long id);
}
