package com.example.demo.controller;

import com.example.demo.model.Ingredient;
import com.example.demo.repository.IngredientRepository;
import com.example.demo.repository.PurchaseOrderRepository;
import com.example.demo.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class PurchaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping("/suppliers")
    public ResponseEntity<?> listSuppliers() {
        try {
            return ResponseEntity.ok(supplierRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listPurchases() {
        try {
            String sql = "SELECT po.*, s.SupplierName " +
                         "FROM PurchaseOrder po " +
                         "LEFT JOIN Supplier s ON po.SupplierID = s.SupplierID " +
                         "ORDER BY po.PurchaseDate DESC";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseDetail(@PathVariable Integer id) {
        try {
            String sqlOrder = "SELECT po.*, s.SupplierName, s.ContactInfo " +
                              "FROM PurchaseOrder po " +
                              "LEFT JOIN Supplier s ON po.SupplierID = s.SupplierID " +
                              "WHERE po.PurchaseOrderID = ?";
            var orderList = jdbcTemplate.queryForList(sqlOrder, id);
            if (orderList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var order = orderList.get(0);

            String sqlDetails = "SELECT pod.*, i.Name AS ingredientName, i.Unit " +
                                "FROM PurchaseOrderDetail pod " +
                                "LEFT JOIN Ingredient i ON pod.IngredientID = i.IngredientID " +
                                "WHERE pod.PurchaseOrderID = ?";
            var details = jdbcTemplate.queryForList(sqlDetails, id);
            
            return ResponseEntity.ok(Map.of("order", order, "details", details));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPurchase(@RequestBody Map<String, Object> payload) {
        try {
            Integer supplierId = payload.get("supplierId") != null ? ((Number)payload.get("supplierId")).intValue() : null;
            String supplierName = payload.get("supplierName") != null ? payload.get("supplierName").toString() : null;
            String contact = payload.get("contact") != null ? payload.get("contact").toString() : null;
            List<Map<String,Object>> items = (List<Map<String,Object>>) payload.get("items");

            if (supplierId == null && supplierName != null) {
                // create supplier
                supplierId = supplierRepository.save(supplierName, contact);
            }

            LocalDateTime now = LocalDateTime.now();
            BigDecimal totalAmount = BigDecimal.ZERO;
            int poId = purchaseOrderRepository.createPurchaseOrder(supplierId, now, totalAmount, payload.get("remark") != null ? payload.get("remark").toString() : null);

            for (Map<String,Object> it : items) {
                Integer ingredientId = it.get("ingredientId") != null ? ((Number)it.get("ingredientId")).intValue() : null;
                String name = it.get("name") != null ? it.get("name").toString() : null;
                BigDecimal qty = it.get("quantity") != null ? new BigDecimal(it.get("quantity").toString()) : BigDecimal.ZERO;
                BigDecimal unitCost = it.get("unitCost") != null ? new BigDecimal(it.get("unitCost").toString()) : BigDecimal.ZERO;

                if (ingredientId == null && name != null) {
                    Ingredient ing = new Ingredient();
                    ing.setName(name);
                    ing.setUnit(it.get("unit") != null ? it.get("unit").toString() : "pcs");
                    ing.setDefaultUnitCost(unitCost);
                    ingredientRepository.save(ing);
                    ingredientId = ing.getIngredientId();
                }

                purchaseOrderRepository.createPurchaseOrderDetail(poId, ingredientId, qty, unitCost);
                // tăng tồn kho
                ingredientRepository.incrementStock(ingredientId, qty);
                totalAmount = totalAmount.add(unitCost.multiply(qty));
            }

            // update total amount
            purchaseOrderRepository.updateTotalAmount(poId, totalAmount);

            return ResponseEntity.ok(Map.of("success", true, "purchaseOrderId", poId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
