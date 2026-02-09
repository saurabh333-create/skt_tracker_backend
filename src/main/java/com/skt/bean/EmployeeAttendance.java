package com.skt.bean;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Data
@Table(name = "employee_attendance")
public class EmployeeAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Integer attendanceId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employeeId;

    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;

    @Column(name = "status")
    private String status;

    @Column(name = "remarks")
    private String remarks;

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public void setCheckIn(LocalTime checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(LocalTime checkOut) {
        this.checkOut = checkOut;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public Employee getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Employee employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public LocalTime getCheckIn() {
        return checkIn;
    }

    public LocalTime getCheckOut() {
        return checkOut;
    }

    public String getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }
}
