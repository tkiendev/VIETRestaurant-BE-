package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Bill;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class BillRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Bill> rowMapper = (rs, rowNum) -> {
        Bill bill = new Bill();
        bill.setBillID(rs.getInt("BillID"));
        bill.setTableID(rs.getInt("TableID"));
        bill.setCustomerID(rs.getObject("CustomerID", Integer.class));
        bill.setCashierID(rs.getObject("CashierID", Integer.class));
        bill.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        bill.setDiscount(rs.getBigDecimal("Discount"));

        Timestamp timeInTs = rs.getTimestamp("TimeIn");
        if (timeInTs != null) bill.setTimeIn(timeInTs.toLocalDateTime());

        Timestamp timeOutTs = rs.getTimestamp("TimeOut");
        if (timeOutTs != null) bill.setTimeOut(timeOutTs.toLocalDateTime());

        bill.setStatus(rs.getString("Status"));
        return bill;
    };

    public List<Bill> findAll() {
        String sql = "SELECT * FROM Bill";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Bill> findById(Integer id) {
        String sql = "SELECT * FROM Bill WHERE BillID = ?";
        List<Bill> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Bill> findByTableId(Integer tableId) {
        String sql = "SELECT * FROM Bill WHERE TableID = ? AND Status = 'Chưa thanh toán' ORDER BY TimeIn DESC LIMIT 1";
        List<Bill> results = jdbcTemplate.query(sql, rowMapper, tableId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Bill> findByStatus(String status) {
        String sql = "SELECT * FROM Bill WHERE Status = ? ORDER BY TimeIn DESC";
        return jdbcTemplate.query(sql, rowMapper, status);
    }

    public Bill save(Bill bill) {
        String sql = "INSERT INTO Bill (TableID, CustomerID, CashierID, TotalAmount, Discount, TimeIn, TimeOut, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, bill.getTableID());
            ps.setObject(2, bill.getCustomerID());
            ps.setObject(3, bill.getCashierID());
            ps.setBigDecimal(4, bill.getTotalAmount());
            ps.setBigDecimal(5, bill.getDiscount() != null ? bill.getDiscount() : BigDecimal.ZERO);
            ps.setTimestamp(6, Timestamp.valueOf(bill.getTimeIn() != null ? bill.getTimeIn() : LocalDateTime.now()));
            ps.setObject(7, bill.getTimeOut() != null ? Timestamp.valueOf(bill.getTimeOut()) : null);
            ps.setString(8, bill.getStatus() != null ? bill.getStatus() : "Chưa thanh toán");
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) bill.setBillID(keyHolder.getKey().intValue());
        return bill;
    }

    public int update(Integer id, Bill bill) {
        String sql = "UPDATE Bill SET TableID = ?, CustomerID = ?, CashierID = ?, TotalAmount = ?, Discount = ?, TimeIn = ?, TimeOut = ?, Status = ? WHERE BillID = ?";
        return jdbcTemplate.update(sql,
                bill.getTableID(),
                bill.getCustomerID(),
                bill.getCashierID(),
                bill.getTotalAmount(),
                bill.getDiscount() != null ? bill.getDiscount() : BigDecimal.ZERO,
                bill.getTimeIn() != null ? Timestamp.valueOf(bill.getTimeIn()) : null,
                bill.getTimeOut() != null ? Timestamp.valueOf(bill.getTimeOut()) : null,
                bill.getStatus(),
                id);
    }

    public int updateStatus(Integer id, String status) {
        String sql = "UPDATE Bill SET Status = ? WHERE BillID = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    public int updateStatusAndTimeOut(Integer id, String status) {
        String sql = "UPDATE Bill SET Status = ?, TimeOut = ? WHERE BillID = ?";
        return jdbcTemplate.update(sql, status, Timestamp.valueOf(LocalDateTime.now()), id);
    }

    public int updateCustomer(Integer id, Integer customerId) {
        String sql = "UPDATE Bill SET CustomerID = ? WHERE BillID = ?";
        return jdbcTemplate.update(sql, customerId, id);
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM Bill WHERE BillID = ?", id);
    }
}
