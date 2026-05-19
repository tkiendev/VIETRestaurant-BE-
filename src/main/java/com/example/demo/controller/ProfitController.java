package com.example.demo.controller;

import com.example.demo.model.MenuItemProfit;
import com.example.demo.model.SalesProfit;
import com.example.demo.repository.ProfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profit")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class ProfitController {

    @Autowired
    private ProfitRepository profitRepository;

    @GetMapping("/menu-items")
    public ResponseEntity<?> getMenuItemProfits() {
        try {
            List<MenuItemProfit> list = profitRepository.findMenuItemProfits();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching menu item profits: " + e.getMessage());
        }
    }

    @GetMapping("/sales-summary")
    public ResponseEntity<?> getSalesProfitSummary() {
        try {
            List<SalesProfit> list = profitRepository.findSalesProfitSummary();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching sales profit summary: " + e.getMessage());
        }
    }
}
