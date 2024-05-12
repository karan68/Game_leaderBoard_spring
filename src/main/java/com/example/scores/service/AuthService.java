package com.example.scores.service;

import com.example.scores.entity.Admin;
import com.example.scores.entity.Employee;
import com.example.scores.repository.AdminRepository;
import com.example.scores.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Map<String, String> authenticatedUsers = new HashMap<>();

    public String authenticateAdmin(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            authenticatedUsers.put(username, "ADMIN");
            return "Admin authenticated";
        }
        return "Invalid credentials";
    }

    public String authenticateEmployee(String username, String password) {
        Employee employee = employeeRepository.findByUsername(username);
        if (employee != null && employee.getPassword().equals(password)) {
            authenticatedUsers.put(username, "EMPLOYEE");
            return "Employee authenticated";
        }
        return "Invalid credentials";
    }

    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public String getRole(String username) {
        return authenticatedUsers.get(username);
    }

    public void logout(String username) {
        authenticatedUsers.remove(username);
    }
}