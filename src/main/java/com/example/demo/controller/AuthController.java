package com.example.demo.controller;

import com.example.demo.config.JwtUtils;
import com.example.demo.model.Account;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Account> accountOpt = accountRepository.findByUsername(loginRequest.getUsername());

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getIsActive() && loginRequest.getPassword().equals(account.getPassword())) {
                
                String roleName = "";
                if (account.getRoleID() != null) {
                    Optional<Role> roleOpt = roleRepository.findById(account.getRoleID());
                    if (roleOpt.isPresent()) {
                        roleName = roleOpt.get().getRoleName();
                    }
                }

                User user = null;
                Optional<User> userOpt = userRepository.findByAccountId(account.getAccountID());
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                }

                String token = jwtUtils.generateToken(account.getUsername(), roleName, user != null ? user.getUserID() : null);

                return ResponseEntity.ok(new LoginResponse(token, user, roleName));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác");
    }

    // =====================================================================
    // ĐĂNG KÝ TÀI KHOẢN KHÁCH HÀNG
    // =====================================================================
    @PostMapping("/register-customer")
    @CrossOrigin(origins = "${cors.allowed-origins}")
    @Transactional
    public ResponseEntity<?> registerCustomer(@RequestBody java.util.Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String fullName = (String) body.get("fullName");
        String phone    = (String) body.get("phone");
        String email    = (String) body.get("email");

        // Validate
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of("success", false, "message", "Vui lòng điền đầy đủ thông tin bắt buộc")
            );
        }

        // Kiểm tra username đã tồn tại chưa
        if (accountRepository.findByUsername(username.trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                java.util.Map.of("success", false, "message", "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.")
            );
        }

        // Kiểm tra số điện thoại nếu có
        if (phone != null && !phone.trim().isEmpty()) {
            if (userRepository.findByPhone(phone.trim()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    java.util.Map.of("success", false, "message", "Số điện thoại đã được đăng ký.")
                );
            }
        }

        // Tạo Account (roleID = null = khách hàng, không phải nhân viên)
        Account account = new Account(null, username.trim(), password, null, true);
        Account savedAccount = accountRepository.save(account);

        // Tạo User Customer liên kết với Account
        User user = new User(null, savedAccount.getAccountID(), "Customer",
            fullName.trim(),
            phone != null ? phone.trim() : null,
            email != null ? email.trim() : null,
            null, 0, "Thường", null);
        User savedUser = userRepository.save(user);

        // Tạo token đăng nhập ngay sau đăng ký
        String token = jwtUtils.generateToken(username.trim(), "customer", savedUser.getUserID());

        return ResponseEntity.ok(java.util.Map.of(
            "success", true,
            "message", "Đăng ký thành công! Chào mừng " + fullName,
            "token", token,
            "user", savedUser,
            "roleName", "customer"
        ));
    }
}

