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
            customer.setFullName("Khách vãng lai");
        } else if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
            customer.setFullName("Khách vãng lai (" + customer.getPhone().trim() + ")");
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Integer id, @RequestBody User customerDetails) {
        Optional<User> opt = userRepository.findById(id);
        if (!opt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User existing = opt.get();
        existing.setFullName(customerDetails.getFullName());
        existing.setPhone(customerDetails.getPhone());
        existing.setEmail(customerDetails.getEmail());
        existing.setAddress(customerDetails.getAddress());
        userRepository.update(id, existing);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật khách hàng thành công",
                "customer", existing
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer id) {
        Optional<User> opt = userRepository.findById(id);
        if (!opt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Xóa khách hàng thành công"
        ));
    }
}

