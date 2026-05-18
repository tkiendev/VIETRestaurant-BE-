package com.example.demo.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.MenuItem;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. Lấy thông tin Món ăn
    public MenuItem getMenuItemById(Integer itemId) {
        String sql = "SELECT MenuItemID, ItemName, Price FROM MenuItem WHERE MenuItemID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            MenuItem item = new MenuItem();
            item.setMenuItemId(rs.getInt("MenuItemID"));
            item.setItemName(rs.getString("ItemName"));
            item.setPrice(rs.getBigDecimal("Price"));
            return item;
        }, itemId);
    }

    // 2. Cập nhật trạng thái Bàn
    public void updateTableStatus(Integer tableId, String status) {
        String sql = "UPDATE DiningTable SET Status = ? WHERE TableID = ?";
        jdbcTemplate.update(sql, status, tableId);
    }

    // 3. Tìm Bill chưa thanh toán của bàn
    public Integer findUnpaidBillByTableId(Integer tableId) {
        String sql = "SELECT BillID FROM Bill WHERE TableID = ? AND Status = 'Chưa thanh toán' LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, tableId);
        } catch (Exception e) {
            return null; // Không tìm thấy
        }
    }

    // 4. Tạo Bill mới và trả về BillID
    public Integer createNewBill(Integer tableId) {
        String sql = "INSERT INTO Bill (TableID, TotalAmount, Status, TimeIn) VALUES (?, 0, 'Chưa thanh toán', NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, tableId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 5. Thêm chi tiết hóa đơn (BillDetail) và trả về BillDetailID
    public Integer createBillDetail(Integer billId, Integer menuItemId, Integer quantity, BigDecimal unitPrice) {
        String sql = "INSERT INTO BillDetail (BillID, MenuItemID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, billId);
            ps.setInt(2, menuItemId);
            ps.setInt(3, quantity);
            ps.setBigDecimal(4, unitPrice);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 6. Thêm Order xuống Bếp (KitchenOrder) và trả về KitchenOrderID
    public Integer createKitchenOrder(Integer billDetailId, String note) {
        String sql = "INSERT INTO KitchenOrder (BillDetailID, Status, SpecialNote, ReceivedTime) VALUES (?, 'Chờ chế biến', ?, NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, billDetailId);
            ps.setString(2, note);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 7. Cập nhật tổng tiền của Bill
    public void addTotalAmountToBill(Integer billId, BigDecimal additionalAmount) {
        String sql = "UPDATE Bill SET TotalAmount = TotalAmount + ? WHERE BillID = ?";
        jdbcTemplate.update(sql, additionalAmount, billId);
    }
}
