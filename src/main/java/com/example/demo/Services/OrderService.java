package com.example.demo.Services;

import com.example.demo.model.BillDetail;
import com.example.demo.model.KitchenOrder;
import com.example.demo.model.MenuItem;
import com.example.demo.repository.BillDetailRepository;
import com.example.demo.repository.KitchenOrderRepository;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private KitchenOrderRepository kitchenOrderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private DiningTableService diningTableService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<KitchenOrder> createOrder(Integer billId, Integer tableId, List<Map<String, Object>> menuItems) throws IOException {
        List<KitchenOrder> createdOrders = new ArrayList<>();

        for (Map<String, Object> item : menuItems) {
            Integer menuItemId = ((Number) item.get("menuItemId")).intValue();
            Integer quantity = ((Number) item.get("quantity")).intValue();
            String specialNote = (String) item.get("specialNote");

            MenuItem menuItem = menuItemRepository.findById(menuItemId).orElse(null);
            if (menuItem == null) {
                throw new IllegalArgumentException("Menu item not found: " + menuItemId);
            }

            BillDetail billDetail = new BillDetail();
            billDetail.setBillID(billId);
            billDetail.setMenuItemID(menuItemId);
            billDetail.setQuantity(quantity);
            billDetail.setUnitPrice(menuItem.getPrice());
            billDetail.setSpecialNote(specialNote);

            BillDetail savedDetail = billDetailRepository.save(billDetail);

            KitchenOrder kitchenOrder = new KitchenOrder();
            kitchenOrder.setBillDetailID(savedDetail.getBillDetailID());
            kitchenOrder.setStatus("Chờ chế biến");
            kitchenOrder.setSpecialNote(specialNote);
            kitchenOrder.setReceivedTime(LocalDateTime.now());

            KitchenOrder savedOrder = kitchenOrderRepository.save(kitchenOrder);
            createdOrders.add(savedOrder);

            broadcastOrderCreated(savedOrder, savedDetail, menuItem, tableId);
        }

        diningTableService.updateTableStatus(tableId, "Đang phục vụ");
        return createdOrders;
    }

    private void broadcastOrderCreated(KitchenOrder kitchenOrder, BillDetail billDetail, MenuItem menuItem, Integer tableId) throws IOException {
        Map<String, Object> orderEvent = new HashMap<>();
        orderEvent.put("event", "order_created");
        orderEvent.put("kitchenOrderId", kitchenOrder.getKitchenOrderID());
        orderEvent.put("billDetailId", billDetail.getBillDetailID());
        orderEvent.put("billId", billDetail.getBillID());
        orderEvent.put("menuItemId", menuItem.getMenuItemId());
        orderEvent.put("itemName", menuItem.getItemName());
        orderEvent.put("quantity", billDetail.getQuantity());
        orderEvent.put("unitPrice", billDetail.getUnitPrice());
        orderEvent.put("specialNote", billDetail.getSpecialNote());
        orderEvent.put("tableId", tableId);
        orderEvent.put("timestamp", LocalDateTime.now().toString());

        sessionManager.broadcastToAll(orderEvent);
    }
}
