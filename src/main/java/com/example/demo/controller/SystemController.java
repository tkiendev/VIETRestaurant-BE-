package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "http://localhost:5173")
public class SystemController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private KitchenOrderRepository kitchenOrderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private DiningTableRepository diningTableRepository;

    @GetMapping("/active-state")
    public ResponseEntity<?> getActiveState() {
        try {
            List<Bill> activeBills = billRepository.findByStatus("Chưa thanh toán");
            
            List<Map<String, Object>> tablesState = new ArrayList<>();
            List<Map<String, Object>> kitchenQueue = new ArrayList<>();

            for (Bill bill : activeBills) {
                DiningTable table = diningTableRepository.findById(bill.getTableID()).orElse(null);
                String tableName = table != null ? table.getTableName() : "Bàn " + bill.getTableID();

                List<BillDetail> details = billDetailRepository.findByBillId(bill.getBillID());
                List<KitchenOrder> kitchenOrders = kitchenOrderRepository.findByBillId(bill.getBillID());

                List<Map<String, Object>> orderedItems = new ArrayList<>();

                for (KitchenOrder ko : kitchenOrders) {
                    BillDetail bd = details.stream().filter(d -> d.getBillDetailID().equals(ko.getBillDetailID())).findFirst().orElse(null);
                    if (bd == null) continue;

                    MenuItem mi = menuItemRepository.findById(bd.getMenuItemID()).orElse(null);
                    String itemName = mi != null ? mi.getItemName() : "Món " + bd.getMenuItemID();

                    Map<String, Object> ticket = new HashMap<>();
                    ticket.put("ticketId", ko.getKitchenOrderID());
                    ticket.put("tableId", bill.getTableID());
                    ticket.put("tableName", tableName);
                    ticket.put("itemId", bd.getMenuItemID());
                    ticket.put("itemName", itemName);
                    ticket.put("qty", bd.getQuantity());
                    ticket.put("price", bd.getUnitPrice());
                    ticket.put("note", bd.getSpecialNote());
                    
                    String status = "pending";
                    if ("Đang chế biến".equals(ko.getStatus())) status = "cooking";
                    if ("Hoàn thành".equals(ko.getStatus())) status = "done";
                    ticket.put("status", status);
                    
                    String time = ko.getReceivedTime() != null ? 
                        String.format("%02d:%02d", ko.getReceivedTime().getHour(), ko.getReceivedTime().getMinute()) : "";
                    ticket.put("time", time);

                    orderedItems.add(ticket);
                    if (!"done".equals(status)) {
                        kitchenQueue.add(ticket);
                    } else {
                        // Even if done, usually kitchen queue might hide it, but the frontend currently doesn't filter 'done' from kitchen queue.
                        // Actually, KitchenPage shows 'done' items. Let's add it.
                        kitchenQueue.add(ticket);
                    }
                }

                Map<String, Object> tableState = new HashMap<>();
                tableState.put("id", bill.getTableID());
                tableState.put("name", tableName);
                tableState.put("status", "serving");
                tableState.put("currentBillId", bill.getBillID());
                tableState.put("orderedItems", orderedItems);
                
                double total = orderedItems.stream().mapToDouble(item -> 
                    ((java.math.BigDecimal)item.get("price")).doubleValue() * (int)item.get("qty")
                ).sum();
                tableState.put("total", total);

                tablesState.add(tableState);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tables", tablesState);
            response.put("kitchenQueue", kitchenQueue);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
