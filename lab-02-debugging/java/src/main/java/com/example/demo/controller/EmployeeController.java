package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public List<Employee> getAllEmployees() {
        logger.info("Entrada: GET /api/employees");
        List<Employee> employees = employeeService.getAllEmployees();
        logger.info("Salida: {}", employees);
        return employees;
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        logger.info("Entrada: GET /api/employees/{}", id);
        Employee employee = employeeService.getEmployeeById(id);
        logger.info("Salida: {}", employee);
        return employee;
    }

    @GetMapping("/email/{email}")
    public Employee getEmployeeByEmail(@PathVariable String email) {
        logger.info("Entrada: GET /api/employees/email/{}", email);
        Employee employee = employeeService.findEmployeeByEmail(email);
        logger.info("Salida: {}", employee);
        return employee;
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        logger.info("Entrada: POST /api/employees");
        Employee existing = employeeService.findEmployeeByEmail(employee.getEmail());
        if (existing != null) {
            logger.info("Salida: duplicate email - {}", employee.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Employee createdEmployee = employeeService.saveEmployee(employee);
        logger.info("Salida: {}", createdEmployee);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        logger.info("Entrada: PUT /api/employees/{}", id);
        Employee existingEmployee = employeeService.getEmployeeById(id);
        if (existingEmployee != null) {
            existingEmployee.setName(employee.getName());
            existingEmployee.setSurname(employee.getSurname());
            existingEmployee.setEmail(employee.getEmail());
            Employee updatedEmployee = employeeService.saveEmployee(existingEmployee);
            logger.info("Salida: {}", updatedEmployee);
            return updatedEmployee;
        }
        logger.info("Salida: null");
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        logger.info("Entrada: DELETE /api/employees/{}", id);
        employeeService.deleteEmployee(id);
        logger.info("Salida: void");
    }

    @GetMapping("/externalEmployees")
    public List<Employee> getExternalEmployees() {
        logger.info("Entrada: GET /api/employees/GetExternalEmployees");
        List<Employee> externalEmployees = employeeService.getExternalEmployees();
        logger.info("Salida: {}", externalEmployees);
        return externalEmployees;
    }
}