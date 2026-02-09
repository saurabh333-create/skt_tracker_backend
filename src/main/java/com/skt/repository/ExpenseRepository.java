package com.skt.repository;

import com.skt.bean.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findByExpenseDateBetween(
            LocalDate fromDate,
            LocalDate toDate
    );
}
