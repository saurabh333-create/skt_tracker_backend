package com.skt.repository;

import com.skt.bean.Sell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SellRepository extends JpaRepository<Sell, Integer> {

    List<Sell> findBySellDateBetween(
            LocalDate fromDate,
            LocalDate toDate
    );
}
