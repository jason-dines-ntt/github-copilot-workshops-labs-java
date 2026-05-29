package com.example.demo;

import com.example.demo.controller.EmployeeController;
import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setEmail("john.doe@example.com");
    }

    // --- GET /api/employees ---

    @Test
    void getAllEmployees_shouldReturn200AndListOfEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[0].surname", is("Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void getAllEmployees_shouldReturn200AndEmptyList_whenNoEmployeesExist() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/employees")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- GET /api/employees/{id} ---

    @Test
    void getEmployeeById_shouldReturn200AndEmployee_whenFound() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.surname", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    @Test
    void getEmployeeById_shouldReturn200WithNullBody_whenNotFound() throws Exception {
        when(employeeService.getEmployeeById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/employees/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(employeeService, times(1)).getEmployeeById(99L);
    }

    // --- POST /api/employees ---

    @Test
    void createEmployee_shouldReturn200AndCreatedEmployee() throws Exception {
        Employee newEmployee = new Employee();
        newEmployee.setName("Jane");
        newEmployee.setSurname("Smith");
        newEmployee.setEmail("jane.smith@example.com");

        Employee savedEmployee = new Employee();
        savedEmployee.setId(2L);
        savedEmployee.setName("Jane");
        savedEmployee.setSurname("Smith");
        savedEmployee.setEmail("jane.smith@example.com");

        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(savedEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Jane")))
                .andExpect(jsonPath("$.surname", is("Smith")))
                .andExpect(jsonPath("$.email", is("jane.smith@example.com")));

        verify(employeeService, times(1)).saveEmployee(any(Employee.class));
    }

    @Test
    void createEmployee_shouldReturn400_whenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeeService);
    }

    // --- GET /api/employees/email/{email} ---
    
     @Test
     void getEmployeeByEmail_shouldReturn200AndEmployee_whenFound() throws Exception {
         when(employeeService.getEmployeeByEmail("john.doe@example.com")).thenReturn(employee);

            mockMvc.perform(get("/api/employees/email/john.doe@example.com")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name", is("John")))
                    .andExpect(jsonPath("$.surname", is("Doe")))
                    .andExpect(jsonPath("$.email", is("john.doe@example.com")));

            verify(employeeService, times(1)).getEmployeeByEmail("john.doe@example.com");
        }

        @Test
        void getEmployeeByEmail_shouldReturn200WithNullBody_whenNotFound() throws Exception {
            when(employeeService.getEmployeeByEmail("unknown@example.com")).thenReturn(null);

            mockMvc.perform(get("/api/employees/email/unknown@example.com")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));

            verify(employeeService, times(1)).getEmployeeByEmail("unknown@example.com");
        }

}
