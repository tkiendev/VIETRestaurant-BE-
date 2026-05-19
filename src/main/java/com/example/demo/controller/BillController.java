package com.example.demo.controller;

import com.example.demo.Services.BillService;
import com.example.demo.Services.KitchenOrderService;
import com.example.demo.model.Bill;
import com.example.demo.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private KitchenOrderService kitchenOrderService;

    @Autowired
    private BillRepository billRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createBill(
            @RequestParam Integer tableId,
            @RequestParam(required = false) Integer customerId) {
        try {
            Bill bill = billService.createBill(tableId, customerId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Bill created successfully",
                    "bill", bill
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to create bill: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{billId}/customer")
    public ResponseEntity<?> assignCustomerToBill(
            @PathVariable Integer billId,
            @RequestParam Integer customerId) {
        try {
            Bill bill = billService.assignCustomerToBill(billId, customerId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Customer assigned to bill",
                    "bill", bill
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{billId}")
    public ResponseEntity<?> getBillById(@PathVariable Integer billId) {
        try {
            Bill bill = billService.getBillById(billId);
            if (bill == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to get bill: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getInvoiceHistory() {
        try {
            List<Map<String, Object>> history = billService.getInvoiceHistory();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to fetch invoice history: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/detail/{billId}")
    public ResponseEntity<?> getBillDetail(@PathVariable Integer billId) {
        try {
            Map<String, Object> detail = billService.getBillDetail(billId);
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to fetch bill detail: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getBillStats() {
        try {
            Map<String, Object> stats = billService.getBillStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to fetch bill stats: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{billId}/payment")
    public ResponseEntity<?> processPayment(
            @PathVariable Integer billId,
            @RequestParam String paymentMethod,
            @RequestParam BigDecimal amountPaid) {
        try {
            Bill bill = billService.processPayment(billId, paymentMethod, amountPaid);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment processed successfully",
                    "bill", bill
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to process payment: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{billId}/kitchen-orders/{kitchenOrderId}/status")
    public ResponseEntity<?> updateKitchenOrderStatus(
            @PathVariable Integer kitchenOrderId,
            @RequestParam String status) {
        try {
            var order = kitchenOrderService.updateOrderStatus(kitchenOrderId, status);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Kitchen order status updated",
                    "order", order
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to update status: " + e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{billId}/kitchen-orders/{kitchenOrderId}")
    public ResponseEntity<?> cancelKitchenOrder(
            @PathVariable Integer billId,
            @PathVariable Integer kitchenOrderId) {
        try {
            kitchenOrderService.cancelKitchenOrder(kitchenOrderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Kitchen order cancelled successfully"
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to cancel kitchen order: " + e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}

