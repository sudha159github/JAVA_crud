package com.crudapp.controller;

import com.crudapp.entity.Employee;
import com.crudapp.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    // Get all employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(repository.findAll());
    }

    // Count employees
    @GetMapping("/count")
    public ResponseEntity<Long> countEmployees() {
        long count = repository.findAll()
                .stream()
                .count();

        return ResponseEntity.ok(count);
    }

    // Sort by age
    @GetMapping("/sorted-by-age")
    public ResponseEntity<List<Employee>> sortByAge() {

        List<Employee> employees = repository.findAll()
                .stream()
                .sorted(Comparator.comparing(Employee::getAge))
                .toList();

        return ResponseEntity.ok(employees);
    }

    // Find IT employees
    @GetMapping("/it")
    public ResponseEntity<List<Employee>> getITEmployees() {

        List<Employee> employees = repository.findAll()
                .stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase("IT"))
                .toList();

        return ResponseEntity.ok(employees);
    }

    // Find HR employees older than age
    @GetMapping("/hr/{age}")
    public ResponseEntity<List<Employee>> getHREmployees(
            @PathVariable int age) {

        List<Employee> employees = repository.findAll()
                .stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase("HR"))
                .filter(e -> e.getAge() > age)
                .toList();

        return ResponseEntity.ok(employees);
    }

    // Company email employees
    @GetMapping("/company-emails")
    public ResponseEntity<List<String>> companyEmails() {

        List<String> result = repository.findAll()
                .stream()
                .filter(e -> e.getEmail().endsWith("company.com"))
                .map(Employee::getName)
                .toList();

        return ResponseEntity.ok(result);
    }

    // IT employees >25 sorted, names only
    @GetMapping("/it-above25")
    public ResponseEntity<List<String>> getITEmployeesAbove25() {

        List<String> result = repository.findAll()
                .stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase("IT"))
                .filter(e -> e.getAge() > 25)
                .sorted(Comparator.comparing(Employee::getAge))
                .map(Employee::getName)
                .toList();

        return ResponseEntity.ok(result);
    }

    // Reduce total IT age
    @GetMapping("/it-total-age")
    public ResponseEntity<Integer> totalITAge() {

        Integer total = repository.findAll()
                .stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase("IT"))
                .map(Employee::getAge)
                .reduce(0, Integer::sum);

        return ResponseEntity.ok(total);
    }

    // First company.com employee
    @GetMapping("/first-company")
    public ResponseEntity<?> firstCompanyEmployee() {

        Optional<Employee> emp = repository.findAll()
                .stream()
                .filter(e -> e.getEmail().endsWith("company.com"))
                .findFirst();

        if (emp.isPresent()) {

            Employee e = emp.get();

            return ResponseEntity.ok(
                    Map.of(
                            "name", e.getName(),
                            "email", e.getEmail(),
                            "department", e.getDepartment()
                    )
            );
        }

        return ResponseEntity.ok("Employee not found");
    }

    // Search by name
    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployee(
            @RequestParam String name) {

        List<Employee> employees = repository.findAll()
                .stream()
                .filter(e -> e.getName()
                        .toLowerCase()
                        .contains(name.toLowerCase()))
                .toList();

        return ResponseEntity.ok(employees);
    }

    // Add employee
    @PostMapping
    public ResponseEntity<Employee> addEmployee(
            @RequestBody Employee employee) {

        Employee saved = repository.save(employee);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    // Get employee by id
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update employee
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @RequestBody Employee employee) {

        Optional<Employee> optional =
                repository.findById(id);

        if (optional.isPresent()) {

            Employee existing = optional.get();

            existing.setName(employee.getName());
            existing.setEmail(employee.getEmail());
            existing.setSalary(employee.getSalary());
            existing.setAge(employee.getAge());
            existing.setDepartment(employee.getDepartment());

            return ResponseEntity.ok(
                    repository.save(existing));
        }

        return ResponseEntity.notFound().build();
    }

    // Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long id) {

        if (repository.existsById(id)) {

            repository.deleteById(id);

            return ResponseEntity.ok(
                    "Employee deleted successfully");
        }

        return ResponseEntity.notFound().build();
    }
}