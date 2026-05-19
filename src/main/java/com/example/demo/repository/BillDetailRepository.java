package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.BillDetail;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class BillDetailRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<BillDetail> rowMapper = (rs, rowNum) -> {
        BillDetail detail = new BillDetail();
        detail.setBillDetailID(rs.getInt("BillDetailID"));
        detail.setBillID(rs.getInt("BillID"));
        detail.setMenuItemID(rs.getInt("MenuItemID"));
        detail.setQuantity(rs.getInt("Quantity"));
        detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        detail.setCostPrice(rs.getBigDecimal("CostPrice"));
        // SpecialNote không có trong bảng BillDetail, chỉ có trong KitchenOrder
        return detail;
    };

    public List<BillDetail> findAll() {
        String sql = "SELECT * FROM BillDetail";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<BillDetail> findById(Integer id) {
        String sql = "SELECT * FROM BillDetail WHERE BillDetailID = ?";
        List<BillDetail> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<BillDetail> findByBillId(Integer billId) {
        String sql = "SELECT * FROM BillDetail WHERE BillID = ?";
        return jdbcTemplate.query(sql, rowMapper, billId);
    }

    public BillDetail save(BillDetail detail) {
        String sql = "INSERT INTO BillDetail (BillID, MenuItemID, Quantity, UnitPrice, CostPrice) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, detail.getBillID());
            ps.setInt(2, detail.getMenuItemID());
            ps.setInt(3, detail.getQuantity());
            ps.setBigDecimal(4, detail.getUnitPrice());
            ps.setBigDecimal(5, detail.getCostPrice() != null ? detail.getCostPrice() : BigDecimal.ZERO);
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) detail.setBillDetailID(keyHolder.getKey().intValue());
        return detail;
    }

    public int update(Integer id, BillDetail detail) {
        String sql = "UPDATE BillDetail SET BillID = ?, MenuItemID = ?, Quantity = ?, UnitPrice = ?, CostPrice = ? WHERE BillDetailID = ?";
        return jdbcTemplate.update(sql,
                detail.getBillID(),
                detail.getMenuItemID(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getCostPrice() != null ? detail.getCostPrice() : BigDecimal.ZERO,
                id);
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM BillDetail WHERE BillDetailID = ?", id);
    }

    public int deleteByBillId(Integer billId) {
        return jdbcTemplate.update("DELETE FROM BillDetail WHERE BillID = ?", billId);
    }
}
