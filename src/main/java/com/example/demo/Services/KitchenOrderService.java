package com.example.demo.Services;

import com.example.demo.model.KitchenOrder;
import com.example.demo.model.BillDetail;
import com.example.demo.model.MenuItem;
import com.example.demo.repository.KitchenOrderRepository;
import com.example.demo.repository.BillDetailRepository;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KitchenOrderService {

    @Autowired
    private KitchenOrderRepository kitchenOrderRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private SessionManager sessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<KitchenOrder> getPendingOrders() {
        return kitchenOrderRepository.findPending();
    }

    public Optional<KitchenOrder> getOrderById(Integer kitchenOrderId) {
        return kitchenOrderRepository.findById(kitchenOrderId);
    }

    public KitchenOrder updateOrderStatus(Integer kitchenOrderId, String newStatus) throws IOException {
        Optional<KitchenOrder> orderOpt = kitchenOrderRepository.findById(kitchenOrderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("Kitchen order not found: " + kitchenOrderId);
        }

        KitchenOrder order = orderOpt.get();
        String oldStatus = order.getStatus();

        if ("Hoàn thành".equals(newStatus)) {
            order.setCompletedTime(LocalDateTime.now());
            kitchenOrderRepository.updateStatusAndCompletedTime(kitchenOrderId, newStatus);
        } else {
            kitchenOrderRepository.updateStatus(kitchenOrderId, newStatus);
            order.setStatus(newStatus);
        }

        broadcastStatusUpdate(order, oldStatus, newStatus);
        return order;
    }

    private void broadcastStatusUpdate(KitchenOrder order, String oldStatus, String newStatus) throws IOException {
        Optional<BillDetail> billDetailOpt = billDetailRepository.findById(order.getBillDetailID());
        if (!billDetailOpt.isPresent()) return;

        BillDetail billDetail = billDetailOpt.get();
        MenuItem menuItem = menuItemRepository.findById(billDetail.getMenuItemID()).orElse(null);
        if (menuItem == null) return;

        Integer billId = billDetail.getBillID();
        Integer tableId = billRepository.findById(billId).map(b -> b.getTableID()).orElse(null);

        Map<String, Object> statusEvent = new HashMap<>();
        statusEvent.put("event", "kitchen_status_updated");
        statusEvent.put("kitchenOrderId", order.getKitchenOrderID());
        statusEvent.put("billDetailId", order.getBillDetailID());
        statusEvent.put("menuItemId", billDetail.getMenuItemID());
        statusEvent.put("itemName", menuItem.getItemName());
        statusEvent.put("quantity", billDetail.getQuantity());
        statusEvent.put("oldStatus", oldStatus);
        statusEvent.put("newStatus", newStatus);
        statusEvent.put("specialNote", order.getSpecialNote());
        statusEvent.put("billId", billId);
        statusEvent.put("tableId", tableId);
        statusEvent.put("completedTime", order.getCompletedTime() != null ? order.getCompletedTime().toString() : null);
        statusEvent.put("timestamp", LocalDateTime.now().toString());

        sessionManager.broadcastToAll(statusEvent);
    }
}
