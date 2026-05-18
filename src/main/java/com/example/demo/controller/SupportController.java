package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/supports")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class SupportController {

    @PostMapping
    public ResponseEntity<?> submitSupportRequest(@RequestBody Map<String, Object> payload) {
        String name = payload.getOrDefault("name", "Khách hàng").toString();
        String email = payload.getOrDefault("email", "").toString();
        String message = payload.getOrDefault("message", "").toString();

        if (message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Vui lòng nhập nội dung hỗ trợ."
            ));
        }

        System.out.println("[Support request] name=" + name + " email=" + email + " message=" + message);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Yêu cầu hỗ trợ của bạn đã được gửi. Chúng tôi sẽ phản hồi sớm.",
                "data", Map.of("name", name, "email", email, "message", message)
        ));
    }
}

