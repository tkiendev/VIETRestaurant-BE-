package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class CustomerController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getCustomers() {
        return ResponseEntity.ok(userRepository.findAllCustomers());
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody User customer) {
        if ((customer.getFullName() == null || customer.getFullName().trim().isEmpty()) &&
                (customer.getPhone() == null || customer.getPhone().trim().isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Vui lòng nhập tên hoặc số điện thoại khách hàng"
            ));
        }

        if (customer.getPhone() != null && !customer.getPhone().trim().isEmpty()) {
            Optional<User> existing = userRepository.findByPhone(customer.getPhone());
            if (existing.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Customer already exists",
                        "customer", existing.get()
                ));
            }
        }

        customer.setUserType("Customer");
        customer.setAccountID(null);
        customer.setRewardPoints(customer.getRewardPoints() != null ? customer.getRewardPoints() : 0);
        if (customer.getCustomerTier() == null) {
            customer.setCustomerTier("Thường");
        }

        User saved = userRepository.save(customer);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Customer created successfully",
                "customer", saved
        ));
    }
}

