package com.example.demo.controller;

import com.example.demo.Services.OrderService;
import com.example.demo.model.KitchenOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @RequestParam Integer billId,
            @RequestParam Integer tableId,
            @RequestBody List<Map<String, Object>> menuItems) {
        try {
            List<KitchenOrder> orders = orderService.createOrder(billId, tableId, menuItems);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order created successfully",
                    "orders", orders
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to create order: " + e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
