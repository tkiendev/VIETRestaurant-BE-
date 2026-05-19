package com.example.demo.Services;

import com.example.demo.model.KitchenOrder;
import com.example.demo.model.BillDetail;
import com.example.demo.model.MenuItem;
import com.example.demo.model.Bill;
import com.example.demo.repository.KitchenOrderRepository;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private com.example.demo.repository.MenuItemIngredientRepository menuItemIngredientRepository;

    @Autowired
    private com.example.demo.repository.IngredientRepository ingredientRepository;

    @Autowired
    private DiningTableService diningTableService;

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

    @Transactional
    public void cancelKitchenOrder(Integer kitchenOrderId) throws IOException {
        Optional<KitchenOrder> orderOpt = kitchenOrderRepository.findById(kitchenOrderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy đơn bếp: " + kitchenOrderId);
        }
        KitchenOrder order = orderOpt.get();

        Optional<BillDetail> billDetailOpt = billDetailRepository.findById(order.getBillDetailID());
        if (!billDetailOpt.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy chi tiết hóa đơn liên quan: " + order.getBillDetailID());
        }
        BillDetail billDetail = billDetailOpt.get();

        Optional<Bill> billOpt = billRepository.findById(billDetail.getBillID());
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn liên quan: " + billDetail.getBillID());
        }
        Bill bill = billOpt.get();

        // 1. Hoàn nguyên liệu vào kho
        List<com.example.demo.model.MenuItemIngredient> recipe = menuItemIngredientRepository.findByMenuItemId(billDetail.getMenuItemID());
        for (com.example.demo.model.MenuItemIngredient r : recipe) {
            java.math.BigDecimal needed = r.getQuantity().multiply(new java.math.BigDecimal(billDetail.getQuantity()));
            ingredientRepository.incrementStock(r.getIngredientId(), needed);
        }

        // 2. Xóa KitchenOrder và BillDetail
        kitchenOrderRepository.deleteById(kitchenOrderId);
        billDetailRepository.deleteById(billDetail.getBillDetailID());

        // 3. Tính toán lại tổng tiền của Bill
        List<BillDetail> remainingDetails = billDetailRepository.findByBillId(bill.getBillID());
        java.math.BigDecimal newTotal = remainingDetails.stream()
                .map(bd -> bd.getUnitPrice().multiply(new java.math.BigDecimal(bd.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        String newTableStatus = "serving"; // mặc định là đang phục vụ
        if (remainingDetails.isEmpty()) {
            // Nếu không còn món ăn nào, xóa Bill
            billRepository.deleteById(bill.getBillID());
            // Cập nhật trạng thái bàn thành Trống
            diningTableService.updateTableStatus(bill.getTableID(), "Trống");
            newTableStatus = "empty";
        } else {
            // Ngược lại, cập nhật tổng tiền mới của Bill
            bill.setTotalAmount(newTotal);
            billRepository.update(bill.getBillID(), bill);
        }

        // 4. Phát đi sự kiện WebSocket báo hủy món
        Map<String, Object> cancelEvent = new HashMap<>();
        cancelEvent.put("event", "kitchen_order_cancelled");
        cancelEvent.put("kitchenOrderId", kitchenOrderId);
        cancelEvent.put("billDetailId", billDetail.getBillDetailID());
        cancelEvent.put("billId", bill.getBillID());
        cancelEvent.put("tableId", bill.getTableID());
        cancelEvent.put("newTableStatus", newTableStatus);
        cancelEvent.put("newTotal", newTotal.doubleValue());
        cancelEvent.put("timestamp", LocalDateTime.now().toString());

        sessionManager.broadcastToAll(cancelEvent);
    }
}
