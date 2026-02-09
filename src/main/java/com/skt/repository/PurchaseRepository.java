package com.skt.repository;

import com.skt.bean.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    List<Purchase> findByPurchaseDateBetween(
            LocalDate fromDate,
            LocalDate toDate
    );
}
