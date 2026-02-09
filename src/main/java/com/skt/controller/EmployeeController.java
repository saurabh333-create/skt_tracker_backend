package com.skt.controller;

import com.skt.bean.Employee;
import com.skt.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    // POST create employee
    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return ResponseEntity.ok(service.createEmployee(employee));
    }

    // GET by name
    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchByName(
            @RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    // GET by email
    @GetMapping("/email")
    public ResponseEntity<Employee> getByEmail(
            @RequestParam String email) {
        return service.getByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
