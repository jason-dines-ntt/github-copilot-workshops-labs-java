# EmployeeController.java Explanation

## Overview

`EmployeeController` is a **Spring Boot REST Controller** that handles HTTP requests for employee-related operations. It is mapped to the `/api/employees` base URL.

---

## Package Declaration

```java
package com.example.demo.controller;
```

This tells the Java compiler that the `EmployeeController` class belongs to the `com.example.demo.controller` package. Think of a package as a **folder** that organizes related classes together.

### Package Naming Convention

Java packages follow a **reverse domain name** convention:

- `com` → top-level domain
- `example` → organization/company name
- `demo` → project/application name
- `controller` → layer or module within the project

This naming strategy ensures **globally unique** package names and avoids class name conflicts.

---

## Base URL

The base URL is `/api/employees`, defined by the `@RequestMapping` annotation on the class:  
`@RequestMapping("/api/employees")`

---

## Endpoints

| Method | URL | Description | Expected Input | Expected Output |
|--------|-----|-------------|----------------|-----------------|
| GET | `/api/employees` | Returns all employees | None | `200 OK` - Array of Employee objects |
| GET | `/api/employees/{id}` | Returns employee by ID | Path variable: `id` (Long) | `200 OK` - Single Employee object |
| GET | `/api/employees/email/{email}` | Returns employee by email | Path variable: `email` (String) | `200 OK` - Single Employee object |
| POST | `/api/employees` | Creates a new employee | Request body: Employee JSON | `200 OK` - Created Employee object |
| PUT | `/api/employees/{id}` | Updates an existing employee | Path variable: `id` (Long) + Request body: Employee JSON | `200 OK` - Updated Employee object, or `null` if not found |
| DELETE | `/api/employees/{id}` | Deletes an employee | Path variable: `id` (Long) | `200 OK` - No content |
| GET | `/api/employees/externalEmployees` | Returns external employees | None | `200 OK` - Array of Employee objects |

### Example Employee JSON (Request Body)

```json
{
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@example.com"
}
```

## Fixed Bugs

### 1. Path Variable Mismatch in `getEmployeeById`

In `getEmployeeById`, the path variable was named `{identificador}` but the method parameter used `@PathVariable Long id`. These names must match, causing a `MissingPathVariableException` at runtime.

```java
// Bug: path variable name mismatch
@GetMapping("/{identificador}")
public Employee getEmployeeById(@PathVariable Long id) { ... }

// Fix:
@GetMapping("/{id}")
public Employee getEmployeeById(@PathVariable Long id) { ... }
```

### 2. Type Mismatch in `Employee.java`

The `email` field was declared as `Long` instead of `String`, causing type errors when handling email values.

```java
// Bug: wrong type for email field
private Long email;

// Fix:
private String email;
```

---

## Project Structure

This follows a **Spring Boot layered architecture**:

- `controller` → Handles HTTP requests/responses
- `service` → Contains business logic
- `model` → Defines data entities