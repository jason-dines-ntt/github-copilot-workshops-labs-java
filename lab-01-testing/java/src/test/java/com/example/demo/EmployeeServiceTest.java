package com.example.demo;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setEmail("john.doe@example.com");
    }

    // --- getAllEmployees ---

    @Test
    void getAllEmployees_shouldReturnListOfEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John");
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getAllEmployees_shouldReturnEmptyList_whenNoEmployeesExist() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).isEmpty();
        verify(employeeRepository, times(1)).findAll();
    }

    // --- getEmployeeById ---

    @Test
    void getEmployeeById_shouldReturnEmployee_whenFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_shouldReturnNull_whenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        Employee result = employeeService.getEmployeeById(99L);

        assertThat(result).isNull();
        verify(employeeRepository, times(1)).findById(99L);
    }

    // --- saveEmployee ---

    @Test
    void saveEmployee_shouldReturnSavedEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee result = employeeService.saveEmployee(employee);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void saveEmployee_shouldPersistAllFields() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee result = employeeService.saveEmployee(employee);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSurname()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
    }

    // --- deleteEmployee ---

    @Test
    void deleteEmployee_shouldInvokeDeleteById() {
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEmployee_shouldNotThrow_whenEmployeeDoesNotExist() {
        doNothing().when(employeeRepository).deleteById(99L);

        employeeService.deleteEmployee(99L);

        verify(employeeRepository, times(1)).deleteById(99L);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void getEmployeeByEmail_shouldReturnEmployee_whenFound() {
        when(employeeRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeByEmail("john.doe@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getSurname()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(employeeRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void getEmployeeByEmail_shouldReturnNull_whenNotFound() {
        when(employeeRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Employee result = employeeService.getEmployeeByEmail("unknown@example.com");
        assertThat(result).isNull();
        verify(employeeRepository, times(1)).findByEmail("unknown@example.com");
    }
}
