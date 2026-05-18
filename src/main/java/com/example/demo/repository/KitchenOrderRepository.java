package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.KitchenOrder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class KitchenOrderRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<KitchenOrder> rowMapper = (rs, rowNum) -> {
        KitchenOrder order = new KitchenOrder();
        order.setKitchenOrderID(rs.getInt("KitchenOrderID"));
        order.setBillDetailID(rs.getInt("BillDetailID"));
        order.setStatus(rs.getString("Status"));
        order.setSpecialNote(rs.getString("SpecialNote"));

        Timestamp receivedTs = rs.getTimestamp("ReceivedTime");
        if (receivedTs != null) order.setReceivedTime(receivedTs.toLocalDateTime());

        Timestamp completedTs = rs.getTimestamp("CompletedTime");
        if (completedTs != null) order.setCompletedTime(completedTs.toLocalDateTime());

        return order;
    };

    public List<KitchenOrder> findAll() {
        String sql = "SELECT * FROM KitchenOrder";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<KitchenOrder> findById(Integer id) {
        String sql = "SELECT * FROM KitchenOrder WHERE KitchenOrderID = ?";
        List<KitchenOrder> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<KitchenOrder> findByBillDetailId(Integer billDetailId) {
        String sql = "SELECT * FROM KitchenOrder WHERE BillDetailID = ?";
        List<KitchenOrder> results = jdbcTemplate.query(sql, rowMapper, billDetailId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<KitchenOrder> findByStatus(String status) {
        String sql = "SELECT * FROM KitchenOrder WHERE Status = ? ORDER BY ReceivedTime ASC";
        return jdbcTemplate.query(sql, rowMapper, status);
    }

    public List<KitchenOrder> findPending() {
        String sql = "SELECT * FROM KitchenOrder WHERE Status IN ('Chờ chế biến', 'Đang nấu') ORDER BY ReceivedTime ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public KitchenOrder save(KitchenOrder order) {
        String sql = "INSERT INTO KitchenOrder (BillDetailID, Status, SpecialNote, ReceivedTime, CompletedTime) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, order.getBillDetailID());
            ps.setString(2, order.getStatus() != null ? order.getStatus() : "Chờ chế biến");
            ps.setString(3, order.getSpecialNote());
            ps.setTimestamp(4, Timestamp.valueOf(order.getReceivedTime() != null ? order.getReceivedTime() : LocalDateTime.now()));
            ps.setObject(5, order.getCompletedTime() != null ? Timestamp.valueOf(order.getCompletedTime()) : null);
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) order.setKitchenOrderID(keyHolder.getKey().intValue());
        return order;
    }

    public int update(Integer id, KitchenOrder order) {
        String sql = "UPDATE KitchenOrder SET BillDetailID = ?, Status = ?, ReceivedTime = ?, CompletedTime = ? WHERE KitchenOrderID = ?";
        return jdbcTemplate.update(sql,
                order.getBillDetailID(),
                order.getStatus(),
                order.getReceivedTime() != null ? Timestamp.valueOf(order.getReceivedTime()) : null,
                order.getCompletedTime() != null ? Timestamp.valueOf(order.getCompletedTime()) : null,
                id);
    }

    public int updateStatus(Integer id, String status) {
        String sql = "UPDATE KitchenOrder SET Status = ? WHERE KitchenOrderID = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public int updateStatusAndCompletedTime(Integer id, String status) {
        String sql = "UPDATE KitchenOrder SET Status = ?, CompletedTime = ? WHERE KitchenOrderID = ?";
        return jdbcTemplate.update(sql, status, Timestamp.valueOf(LocalDateTime.now()), id);
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM KitchenOrder WHERE KitchenOrderID = ?", id);
    }

    public List<KitchenOrder> findByBillId(Integer billId) {
        String sql = "SELECT ko.* FROM KitchenOrder ko " +
                     "INNER JOIN BillDetail bd ON ko.BillDetailID = bd.BillDetailID " +
                     "WHERE bd.BillID = ?";
        return jdbcTemplate.query(sql, rowMapper, billId);
    }
}
