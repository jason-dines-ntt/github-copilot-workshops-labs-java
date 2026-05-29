# EmployeeController - Full Method Documentation

## Overview

`EmployeeController` is a Spring Boot REST controller that exposes HTTP endpoints
for managing `Employee` resources. It delegates all business logic to `EmployeeService`
and uses SLF4J for logging.

---

## Class-Level Annotations

| Annotation | Purpose |
|---|---|
| `@RestController` | Marks this class as a REST controller. Combines `@Controller` and `@ResponseBody`, meaning all methods automatically serialize return values to JSON. |
| `@RequestMapping("/api/employees")` | Sets the base URL path for all endpoints in this class. |

---

## Logging Setup

```java
private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
```

- Uses **SLF4J** (Simple Logging Facade for Java) to create a logger scoped to this class.
- `static final` means one logger instance is shared across all instances of the class (efficient).
- Log messages use `{}` as placeholders, which are lazily evaluated (only formatted if the log level is active), improving performance.
- Each method logs:
  - **Entry point** (`Entrada`) — logs the HTTP method and path before processing.
  - **Exit point** (`Salida`) — logs the result after processing.

> ⚠️ Note: Log messages are in **Spanish** (`Entrada` = Input/Entry, `Salida` = Output/Exit).

---

## Dependency Injection

```java
@Autowired
private EmployeeService employeeService;
```

- `@Autowired` tells Spring to automatically inject an instance of `EmployeeService` at runtime.
- This follows the **Dependency Inversion Principle** — the controller depends on an abstraction (the service), not a concrete implementation.

---

## Methods

---

### 1. `getAllEmployees()`

```java
@GetMapping
public List<Employee> getAllEmployees() {
    logger.info("Entrada: GET /api/employees");
    List<Employee> employees = employeeService.getAllEmployees();
    logger.info("Salida: {}", employees);
    return employees;
}
```

#### Functional Behaviour
- Handles `GET /api/employees`
- Retrieves and returns **all employees** from the data source.
- Returns a JSON array of `Employee` objects.

#### Non-Functional Behaviour
- No input validation required (no parameters).
- Performance depends on the number of records — no pagination implemented, which could be a concern with large datasets.
- Returns an empty list `[]` if no employees exist (Spring/JPA default behaviour).

#### Logging
| Log Statement | When | What it logs |
|---|---|---|
| `Entrada: GET /api/employees` | Before service call | Confirms the endpoint was hit |
| `Salida: {}` | After service call | Logs the full list of employees returned |

---

### 2. `getEmployeeById()`

```java
@GetMapping("/{id}")
public Employee getEmployeeById(@PathVariable Long id) {
    logger.info("Entrada: GET /api/employees/{}", id);
    Employee employee = employeeService.getEmployeeById(id);
    logger.info("Salida: {}", employee);
    return employee;
}
```

#### Functional Behaviour
- Handles `GET /api/employees/{id}`
- Retrieves a **single employee** by their unique ID.
- Returns a single JSON `Employee` object.

#### Non-Functional Behaviour
- Returns `null` if no employee is found — ideally should return `404 Not Found` using `ResponseEntity`.
- No null-check on the returned employee before returning to the client.

#### ✅ Bug Fixed
- The path variable was named `{identificador}` but the method parameter was named `id` with no `@PathVariable("identificador")` mapping.
- This caused a **`MissingPathVariableException`** at runtime. Fixed by renaming the path variable to `{id}`.

```java
// Before (buggy):
@GetMapping("/{identificador}")
public Employee getEmployeeById(@PathVariable Long id) {

// After (fixed):
@GetMapping("/{id}")
public Employee getEmployeeById(@PathVariable Long id) {
```

#### Logging
| Log Statement | When | What it logs |
|---|---|---|
| `Entrada: GET /api/employees/{}` | Before service call | Logs the requested ID |
| `Salida: {}` | After service call | Logs the found Employee object (or null) |

---

### 3. `getEmployeeByEmail()`

```java
@GetMapping("/email/{email}")
public Employee getEmployeeByEmail(@PathVariable String email) {
    logger.info("Entrada: GET /api/employees/email/{}", email);
    Employee employee = employeeService.findEmployeeByEmail(email);
    logger.info("Salida: {}", employee);
    return employee;
}
```

#### Functional Behaviour
- Handles `GET /api/employees/email/{email}`
- Retrieves a **single employee** by their email address.
- Returns a single JSON `Employee` object.

#### Non-Functional Behaviour
- No email format validation — malformed emails will still be passed to the service layer.
- Returns `null` if not found rather than a proper `404` HTTP response.
- Logging email addresses may raise **GDPR/PII compliance concerns** in production environments.

#### Logging
| Log Statement | When | What it logs |
|---|---|---|
| `Entrada: GET /api/employees/email/{}` | Before service call | Logs the requested email address |
| `Salida: {}` | After service call | Logs the found Employee object (or null) |

---

### 4. `createEmployee()`

```java
@PostMapping
public Employee createEmployee(@RequestBody Employee employee) {
    logger.info("Entrada: POST /api/employees");
    Employee createdEmployee = employeeService.saveEmployee(employee);
    logger.info("Salida: {}", createdEmployee);
    return createdEmployee;
}
```

#### Functional Behaviour
- Handles `POST /api/employees`
- Accepts a JSON body representing an `Employee` object.
- Persists the new employee and returns the **saved employee** (including generated ID).

#### Non-Functional Behaviour
- No input validation (e.g., `@Valid`) — invalid or incomplete employee data could be persisted.
- Should ideally return `HTTP 201 Created` using `ResponseEntity` instead of the default `200 OK`.
- The incoming `employee` object is not logged — useful for debugging but be cautious of logging sensitive data in production.

#### Logging
| Log Statement | When | What it logs |
|---|---|---|
| `Entrada: POST /api/employees` | Before service call | Confirms the endpoint was hit (note: request body is NOT logged) |
| `Salida: {}` | After service call | Logs the newly created Employee including its generated ID |

---

### 5. `updateEmployee()`

```java
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
```

#### Functional Behaviour
- Handles `PUT /api/employees/{id}`
- Fetches the existing employee by ID, updates their `name`, `surname`, and `email` fields, then persists the changes.
- Returns the **updated employee** as JSON.
- Returns `null` if no employee with the given ID exists.

#### Non-Functional Behaviour
- Performs **two service calls** (one to fetch, one to save) — could be optimised.
- Returns `null` with a `200 OK` when the employee doesn't exist — should return `404 Not Found`.
- No input validation on the request body fields.
- Only updates three fields (`name`, `surname`, `email`) — other fields are not updated, which could be intentional or a limitation.

#### Logging
| Log Statement | When | What it logs |
|---|---|---|
| `Entrada: PUT /api/employees/{}` | Before processing | Logs the ID of the employee to be updated |
| `Salida: {}` (inside if block) | After successful update | Logs the updated Employee object |
| `Salida: null` (outside if block) | When employee not found | Logs that no employee was found/updated |

---

### 6. `deleteEmployee()`

```java
@DeleteMapping("/{id}")
public void deleteEmployee(@PathVariable Long id) {
    logger.info("Entrada: DELETE /api/employees/{}", id);
    employeeService.deleteEmployee(id);
    logger.info("Salida: void");
}
```

#### Functional Behaviour
- Handles `DELETE /api/employees/{id}`
- Deletes the employee with the specified ID.
- Returns no body (`void`) with a default `200 OK` response.

#### Non-Functional Behaviour
- Returns `200 OK` even if the employee doesn't exist — ideally should return `404 Not Found` or `204 No Content` on success.
- No confirmation that deletion actually occurred.

#### Logging
| Log Statement | When | What it logs |
|---|---|---|
| `Entrada: DELETE /api/employees/{}` | Before service call | Logs the ID of the employee to be deleted |
| `Salida: void` | After service call | Confirms the delete method completed |

---

### 7. `getExternalEmployees()`

```java
@GetMapping("/externalEmployees")
public List<Employee> getExternalEmployees() {
    logger.info("Entrada: GET /api/employees/GetExternalEmployees");
    List<Employee> externalEmployees = employeeService.getExternalEmployees();
    logger.info("Salida: {}", externalEmployees);
    return externalEmployees;
}
```

#### Functional Behaviour
- Handles `GET /api/employees/externalEmployees`
- Retrieves employees from an **external source** (e.g., a third-party API) via the service layer.
- Returns a JSON array of `Employee` objects.

#### Non-Functional Behaviour
- No error handling for external source failures (timeouts, unavailability).
- Performance depends entirely on the external source response time.
- No caching strategy implemented — repeated calls will always hit the external source.

#### Logging
| Log Statement | When | What it logs |
|---|---|---|
| `Entrada: GET /api/employees/GetExternalEmployees` | Before service call | Confirms the endpoint was hit |
| `Salida: {}` | After service call | Logs the full list of externally fetched employees |

---

## Summary of Issues & Recommendations

| Issue | Location | Status | Recommendation |
|---|---|---|---|
| ~~🐛 Path variable mismatch (`{identificador}` vs `id`)~~ | `getEmployeeById` | ✅ Fixed | Renamed to `{id}` |
| ~~🐛 Type mismatch (`Long` instead of `String` for `email`)~~ | `Employee.java` | ✅ Fixed | Changed `email` field type to `String` |
| ⚠️ Returns `null` instead of `404` | Multiple methods | Open | Use `ResponseEntity<Employee>` |
| ⚠️ No input validation | `createEmployee`, `updateEmployee` | Open | Add `@Valid` and bean validation annotations |
| ⚠️ `POST` returns `200 OK` instead of `201 Created` | `createEmployee` | Open | Return `ResponseEntity` with `HttpStatus.CREATED` |
| ⚠️ PII logged (email addresses) | `getEmployeeByEmail` | Open | Mask or remove email from logs in production |
| ⚠️ No pagination | `getAllEmployees` | Open | Consider adding `Pageable` support |