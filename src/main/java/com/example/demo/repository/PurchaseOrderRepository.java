package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.math.BigDecimal;

@Repository
public class PurchaseOrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createPurchaseOrder(Integer supplierId, java.time.LocalDateTime purchaseDate, BigDecimal totalAmount, String remark) {
        String sql = "INSERT INTO PurchaseOrder (SupplierID, PurchaseDate, TotalAmount, Remark) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (supplierId != null) ps.setInt(1, supplierId); else ps.setObject(1, null);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(purchaseDate));
            ps.setBigDecimal(3, totalAmount != null ? totalAmount : BigDecimal.ZERO);
            ps.setString(4, remark);
            return ps;
        }, keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public int createPurchaseOrderDetail(int purchaseOrderId, Integer ingredientId, BigDecimal quantity, BigDecimal unitCost) {
        String sql = "INSERT INTO PurchaseOrderDetail (PurchaseOrderID, IngredientID, Quantity, UnitCost) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, purchaseOrderId);
            if (ingredientId != null) ps.setInt(2, ingredientId); else ps.setObject(2, null);
            ps.setBigDecimal(3, quantity != null ? quantity : BigDecimal.ZERO);
            ps.setBigDecimal(4, unitCost != null ? unitCost : BigDecimal.ZERO);
            return ps;
        }, keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public int updateTotalAmount(int purchaseOrderId, BigDecimal totalAmount) {
        return jdbcTemplate.update("UPDATE PurchaseOrder SET TotalAmount = ? WHERE PurchaseOrderID = ?", totalAmount, purchaseOrderId);
    }
}
