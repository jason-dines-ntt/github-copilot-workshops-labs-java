package com.example.demo;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();

        employee = new Employee();
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setEmail("john.doe@example.com");
    }

    @Test
    void save_shouldPersistEmployee() {
        Employee saved = employeeRepository.save(employee);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("John");
        assertThat(saved.getSurname()).isEqualTo("Doe");
        assertThat(saved.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findAll_shouldReturnAllEmployees() {
        Employee second = new Employee();
        second.setName("Jane");
        second.setSurname("Smith");
        second.setEmail("jane.smith@example.com");

        employeeRepository.save(employee);
        employeeRepository.save(second);

        List<Employee> employees = employeeRepository.findAll();

        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName).containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void findById_shouldReturnEmployee_whenExists() {
        Employee saved = employeeRepository.save(employee);

        Optional<Employee> found = employeeRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Employee> found = employeeRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void delete_shouldRemoveEmployee() {
        Employee saved = employeeRepository.save(employee);

        employeeRepository.delete(saved);

        Optional<Employee> found = employeeRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveEmployee() {
        Employee saved = employeeRepository.save(employee);

        employeeRepository.deleteById(saved.getId());

        assertThat(employeeRepository.findAll()).isEmpty();
    }

    @Test
    void save_shouldUpdateEmployee_whenModified() {
        Employee saved = employeeRepository.save(employee);
        saved.setEmail("updated@example.com");

        Employee updated = employeeRepository.save(saved);

        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(employeeRepository.findAll()).hasSize(1);
    }

    @Test
    void findByEmail_shouldReturnEmployee_whenExists() {
        employeeRepository.save(employee);

        Optional<Employee> found = employeeRepository.findByEmail("john.doe@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John");
        assertThat(found.get().getSurname()).isEqualTo("Doe");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenNotExists() {
        Optional<Employee> found = employeeRepository.findByEmail("unknown@example.com");
        assertThat(found).isEmpty();
    }
}
