package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.EmployeeDTO;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        List<User> employees = userRepository.findAllEmployees();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (User user : employees) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            
            if (user.getAccountID() != null) {
                Optional<Account> accOpt = accountRepository.findById(user.getAccountID());
                if (accOpt.isPresent()) {
                    Account acc = accOpt.get();
                    acc.setPassword(null); // hide password
                    map.put("account", acc);
                    
                    if (acc.getRoleID() != null) {
                        Optional<Role> roleOpt = roleRepository.findById(acc.getRoleID());
                        roleOpt.ifPresent(role -> map.put("roleName", role.getRoleName()));
                    }
                }
            }
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDTO dto) {
        // Kiểm tra username
        if (accountRepository.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username đã tồn tại");
        }

        // 1. Tạo Account
        Account newAccount = new Account();
        newAccount.setUsername(dto.getUsername());
        newAccount.setPassword(dto.getPassword());
        newAccount.setRoleID(dto.getRoleID());
        newAccount.setIsActive(true);
        
        Account savedAccount = accountRepository.save(newAccount);

        // 2. Tạo User
        User newUser = new User();
        newUser.setAccountID(savedAccount.getAccountID());
        newUser.setUserType("Employee");
        newUser.setFullName(dto.getFullName());
        newUser.setPhone(dto.getPhone());
        newUser.setEmail(dto.getEmail());
        newUser.setAddress(dto.getAddress());
        
        User savedUser = userRepository.save(newUser);

        return ResponseEntity.ok("Thêm nhân viên thành công");
    }

    @PutMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> updateEmployee(@PathVariable Integer userId, @RequestBody EmployeeDTO dto) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Không tìm thấy nhân viên");
        }

        User user = userOpt.get();
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        
        userRepository.update(userId, user);

        if (user.getAccountID() != null) {
            Optional<Account> accOpt = accountRepository.findById(user.getAccountID());
            if (accOpt.isPresent()) {
                Account account = accOpt.get();
                if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                    account.setPassword(dto.getPassword());
                }
                account.setRoleID(dto.getRoleID());
                accountRepository.update(account.getAccountID(), account);
            }
        }

        return ResponseEntity.ok("Cập nhật thành công");
    }

    @DeleteMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> deleteEmployee(@PathVariable Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            Integer accId = userOpt.get().getAccountID();
            userRepository.deleteById(userId);
            if (accId != null) {
                accountRepository.deleteById(accId); // Do delete cascade không setup cho DB level theo chiều User -> Account
            }
            return ResponseEntity.ok("Xóa thành công");
        }
        return ResponseEntity.badRequest().body("Không tìm thấy nhân viên");
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }
}
