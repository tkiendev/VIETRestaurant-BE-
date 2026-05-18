package com.example.demo.Services;

import com.example.demo.model.Bill;
import com.example.demo.model.BillDetail;
import com.example.demo.model.MenuItem;
import com.example.demo.model.Payment;
import com.example.demo.model.User;
import com.example.demo.repository.BillDetailRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private DiningTableService diningTableService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Bill createBill(Integer tableId, Integer customerId) {
        Bill bill = new Bill();
        bill.setTableID(tableId);
        bill.setCustomerID(customerId);
        bill.setTotalAmount(BigDecimal.ZERO);
        bill.setTimeIn(LocalDateTime.now());
        bill.setStatus("Chưa thanh toán");
        return billRepository.save(bill);
    }

    public Bill getBillById(Integer billId) {
        return billRepository.findById(billId).orElse(null);
    }

    public Bill assignCustomerToBill(Integer billId, Integer customerId) {
        Optional<Bill> billOpt = billRepository.findById(billId);
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        Bill bill = billOpt.get();
        bill.setCustomerID(customerId);
        billRepository.updateCustomer(billId, customerId);
        return bill;
    }

    public Bill processPayment(Integer billId, String paymentMethod, BigDecimal amountPaid) throws IOException {
        Optional<Bill> billOpt = billRepository.findById(billId);
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }

        Bill bill = billOpt.get();
        bill.setTimeOut(LocalDateTime.now());
        bill.setStatus("Đã thanh toán");
        billRepository.update(billId, bill);

        Payment payment = new Payment();
        payment.setBillID(billId);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmountPaid(amountPaid);
        payment.setPaymentTime(LocalDateTime.now());
        paymentRepository.save(payment);

        broadcastPaymentCompleted(bill, paymentMethod, amountPaid);

        Integer tableId = bill.getTableID();
        diningTableService.updateTableStatus(tableId, "Trống");

        return bill;
    }

    private void broadcastPaymentCompleted(Bill bill, String paymentMethod, BigDecimal amountPaid) throws IOException {
        Map<String, Object> paymentEvent = new HashMap<>();
        paymentEvent.put("event", "payment_completed");
        paymentEvent.put("billId", bill.getBillID());
        paymentEvent.put("tableId", bill.getTableID());
        paymentEvent.put("paymentMethod", paymentMethod);
        paymentEvent.put("amountPaid", amountPaid);
        paymentEvent.put("billTotal", bill.getTotalAmount());
        paymentEvent.put("paymentTime", LocalDateTime.now().toString());

        sessionManager.broadcastToAll(paymentEvent);
    }

    // Lấy lịch sử hoá đơn đã thanh toán (dùng cho Thu ngân & Admin)
    public List<Map<String, Object>> getInvoiceHistory() {
        List<Bill> completedBills = billRepository.findByStatus("Đã thanh toán");
        List<Map<String, Object>> history = new ArrayList<>();

        for (Bill bill : completedBills) {
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("id", "INV-" + bill.getBillID());
            invoice.put("billId", bill.getBillID());

            String tableName = "Bàn " + bill.getTableID();
            Optional<com.example.demo.model.DiningTable> tableOpt = diningTableService.getTableById(bill.getTableID());
            if (tableOpt.isPresent()) {
                tableName = tableOpt.get().getTableName();
            }

            String method = "Tiền mặt";
            double amountPaid = bill.getTotalAmount() != null ? bill.getTotalAmount().doubleValue() : 0.0;
            Optional<Payment> paymentOpt = paymentRepository.findByBillId(bill.getBillID());
            if (paymentOpt.isPresent()) {
                Payment p = paymentOpt.get();
                method = p.getPaymentMethod();
                if (p.getAmountPaid() != null) amountPaid = p.getAmountPaid().doubleValue();
            }

            String mappedMethod = "Tiền mặt".equals(method) ? "cash" : "transfer";

            invoice.put("tableName", tableName);
            invoice.put("total", amountPaid);
            invoice.put("method", mappedMethod);

            if (bill.getCustomerID() != null) {
                Optional<User> customerOpt = userRepository.findById(bill.getCustomerID());
                if (customerOpt.isPresent()) {
                    User customer = customerOpt.get();
                    Map<String, Object> customerMap = new HashMap<>();
                    customerMap.put("id", customer.getUserID());
                    customerMap.put("fullName", customer.getFullName());
                    customerMap.put("phone", customer.getPhone());
                    customerMap.put("address", customer.getAddress());
                    invoice.put("customer", customerMap);
                }
                invoice.put("customerId", bill.getCustomerID());
            }

            String timeStr = bill.getTimeOut() != null ?
                String.format("%02d:%02d %02d/%02d/%d",
                    bill.getTimeOut().getHour(), bill.getTimeOut().getMinute(),
                    bill.getTimeOut().getDayOfMonth(), bill.getTimeOut().getMonthValue(),
                    bill.getTimeOut().getYear()) : "";
            invoice.put("time", timeStr);
            invoice.put("preBooked", false);

            long timestamp = bill.getTimeOut() != null ?
                java.sql.Timestamp.valueOf(bill.getTimeOut()).getTime() : System.currentTimeMillis();
            invoice.put("timestamp", timestamp);

            history.add(invoice);
        }
        return history;
    }

    // Lấy chi tiết 1 hoá đơn kèm danh sách món ăn
    public Map<String, Object> getBillDetail(Integer billId) {
        Optional<Bill> billOpt = billRepository.findById(billId);
        if (!billOpt.isPresent()) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        Bill bill = billOpt.get();
        Map<String, Object> result = new HashMap<>();

        result.put("billId", bill.getBillID());
        result.put("status", bill.getStatus());

        // Tên bàn
        String tableName = "Bàn " + bill.getTableID();
        Optional<com.example.demo.model.DiningTable> tableOpt = diningTableService.getTableById(bill.getTableID());
        if (tableOpt.isPresent()) tableName = tableOpt.get().getTableName();
        result.put("tableName", tableName);

        // Thời gian
        String timeIn = bill.getTimeIn() != null ?
            bill.getTimeIn().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")) : "";
        String timeOut = bill.getTimeOut() != null ?
            bill.getTimeOut().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")) : "";
        result.put("timeIn", timeIn);
        result.put("timeOut", timeOut);

        // Khách hàng
        if (bill.getCustomerID() != null) {
            Optional<User> customerOpt = userRepository.findById(bill.getCustomerID());
            if (customerOpt.isPresent()) {
                User c = customerOpt.get();
                Map<String, Object> cm = new HashMap<>();
                cm.put("id", c.getUserID());
                cm.put("fullName", c.getFullName());
                cm.put("phone", c.getPhone());
                cm.put("email", c.getEmail());
                cm.put("address", c.getAddress());
                result.put("customer", cm);
            }
        }

        // Thanh toán
        Optional<Payment> payOpt = paymentRepository.findByBillId(billId);
        String method = "Tiền mặt";
        double amountPaid = bill.getTotalAmount() != null ? bill.getTotalAmount().doubleValue() : 0.0;
        if (payOpt.isPresent()) {
            method = payOpt.get().getPaymentMethod();
            if (payOpt.get().getAmountPaid() != null) amountPaid = payOpt.get().getAmountPaid().doubleValue();
        }
        result.put("paymentMethod", method);
        result.put("amountPaid", amountPaid);
        result.put("totalAmount", bill.getTotalAmount());

        // Danh sách món ăn trong bill
        List<BillDetail> details = billDetailRepository.findByBillId(billId);
        List<Map<String, Object>> items = new ArrayList<>();
        double computedTotal = 0;
        for (BillDetail bd : details) {
            Map<String, Object> item = new HashMap<>();
            item.put("billDetailId", bd.getBillDetailID());
            item.put("quantity", bd.getQuantity());
            item.put("unitPrice", bd.getUnitPrice());
            double lineTotal = bd.getUnitPrice().doubleValue() * bd.getQuantity();
            item.put("lineTotal", lineTotal);
            computedTotal += lineTotal;

            // Lấy tên món
            String itemName = "Món #" + bd.getMenuItemID();
            Optional<MenuItem> miOpt = menuItemRepository.findById(bd.getMenuItemID());
            if (miOpt.isPresent()) {
                itemName = miOpt.get().getItemName();
                item.put("categoryName", miOpt.get().getCategoryName());
            }
            item.put("itemName", itemName);
            item.put("menuItemId", bd.getMenuItemID());
            items.add(item);
        }
        result.put("items", items);
        result.put("itemCount", items.size());
        result.put("computedTotal", computedTotal);

        return result;
    }

    // Thống kê số bill theo 7 ngày gần nhất
    public Map<String, Object> getBillStats() {
        List<Bill> allBills = billRepository.findAll();
        Map<String, Object> result = new HashMap<>();

        // 7 ngày gần nhất
        List<Map<String, Object>> daily = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            LocalDate nextDay = day.plusDays(1);

            long completed = allBills.stream()
                .filter(b -> "Đã thanh toán".equals(b.getStatus()))
                .filter(b -> b.getTimeOut() != null)
                .filter(b -> {
                    LocalDate billDate = b.getTimeOut().toLocalDate();
                    return !billDate.isBefore(day) && billDate.isBefore(nextDay);
                })
                .count();

            long pending = allBills.stream()
                .filter(b -> "Chưa thanh toán".equals(b.getStatus()))
                .filter(b -> b.getTimeIn() != null)
                .filter(b -> {
                    LocalDate billDate = b.getTimeIn().toLocalDate();
                    return !billDate.isBefore(day) && billDate.isBefore(nextDay);
                })
                .count();

            double revenue = allBills.stream()
                .filter(b -> "Đã thanh toán".equals(b.getStatus()))
                .filter(b -> b.getTimeOut() != null)
                .filter(b -> {
                    LocalDate billDate = b.getTimeOut().toLocalDate();
                    return !billDate.isBefore(day) && billDate.isBefore(nextDay);
                })
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount().doubleValue() : 0)
                .sum();

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", day.format(fmt));
            dayData.put("completed", completed);
            dayData.put("pending", pending);
            dayData.put("revenue", revenue);
            daily.add(dayData);
        }

        result.put("daily", daily);
        result.put("totalCompleted", allBills.stream().filter(b -> "Đã thanh toán".equals(b.getStatus())).count());
        result.put("totalPending", allBills.stream().filter(b -> "Chưa thanh toán".equals(b.getStatus())).count());

        return result;
    }
}
