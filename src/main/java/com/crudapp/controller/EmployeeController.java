package com.crudapp.controller;

import com.crudapp.entity.Employee;
import com.crudapp.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        return repository.save(employee);
    }

    @GetMapping
    public List<Employee> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }
    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id,
                           @RequestBody Employee employee) {

        Employee existing = repository.findById(id).orElse(null);

        if(existing != null) {
            existing.setName(employee.getName());
            existing.setEmail(employee.getEmail());
            existing.setSalary(employee.getSalary());

            return repository.save(existing);
        }

        return null;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        repository.deleteById(id);

        return "Employee deleted successfully";
    }


}