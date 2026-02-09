package com.skt.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long empId;

    @Column(name = "emp_name")
    private String name;

    @Column(name = "emp_code", unique = true)
    private String empCode;

    @Column(name = "emp_type") // R or T
    private String empType;

    @Column(name = "emp_email")
    private String email;

    @Column(name = "emp_joining_date")
    private LocalDate doj;

    @Column(name = "emp_address")
    private String address;

    @Column(name = "emp_position")
    private String position;

    @Column(name = "emp_phone_number")
    private String phone;

    @OneToMany(mappedBy = "employeeId")
    @JsonIgnore
    private List<EmployeeAttendance> attendanceList;

    public List<EmployeeAttendance> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<EmployeeAttendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public Long getEmpId() {
        return empId;
    }

    public String getEmpType() {
        return empType;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public void setEmpType(String empType) {
        this.empType = empType;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDoj() {
        return doj;
    }

    public void setDoj(LocalDate doj) {
        this.doj = doj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
