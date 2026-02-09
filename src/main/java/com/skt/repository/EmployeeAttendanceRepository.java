package com.skt.repository;

import com.skt.bean.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeAttendanceRepository
        extends JpaRepository<EmployeeAttendance, Integer> {

    List<EmployeeAttendance> findByAttendanceDateBetween(
            LocalDate fromDate,
            LocalDate toDate
    );
}
