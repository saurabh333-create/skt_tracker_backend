package com.skt.service;

import com.skt.bean.Employee;
import com.skt.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee createEmployee(Employee emp) {

        // count employees by type
        long count = employeeRepository.countByEmpType(emp.getEmpType());

        // generate code
        String prefix = emp.getEmpType().equalsIgnoreCase("R") ? "R" : "T";
        String code = prefix + String.format("%04d", count + 1);

        emp.setEmpCode(code);

        return employeeRepository.save(emp);
    }
    public List<Employee> searchByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name);
    }

    public Optional<Employee> getByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
}
