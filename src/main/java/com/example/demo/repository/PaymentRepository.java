package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Payment;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Payment> rowMapper = (rs, rowNum) -> {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("PaymentID"));
        payment.setBillID(rs.getInt("BillID"));
        payment.setPaymentMethod(rs.getString("PaymentMethod"));
        payment.setAmountPaid(rs.getBigDecimal("AmountPaid"));

        Timestamp paymentTs = rs.getTimestamp("PaymentTime");
        if (paymentTs != null) payment.setPaymentTime(paymentTs.toLocalDateTime());

        return payment;
    };

    public List<Payment> findAll() {
        String sql = "SELECT * FROM Payment";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Payment> findById(Integer id) {
        String sql = "SELECT * FROM Payment WHERE PaymentID = ?";
        List<Payment> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Payment> findByBillId(Integer billId) {
        String sql = "SELECT * FROM Payment WHERE BillID = ?";
        List<Payment> results = jdbcTemplate.query(sql, rowMapper, billId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Payment save(Payment payment) {
        String sql = "INSERT INTO Payment (BillID, PaymentMethod, AmountPaid, PaymentTime) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, payment.getBillID());
            ps.setString(2, payment.getPaymentMethod());
            ps.setBigDecimal(3, payment.getAmountPaid());
            ps.setTimestamp(4, Timestamp.valueOf(payment.getPaymentTime()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) payment.setPaymentId(keyHolder.getKey().intValue());
        return payment;
    }

    public int update(Integer id, Payment payment) {
        String sql = "UPDATE Payment SET BillID = ?, PaymentMethod = ?, AmountPaid = ?, PaymentTime = ? WHERE PaymentID = ?";
        return jdbcTemplate.update(sql,
                payment.getBillID(),
                payment.getPaymentMethod(),
                payment.getAmountPaid(),
                Timestamp.valueOf(payment.getPaymentTime()),
                id);
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM Payment WHERE PaymentID = ?", id);
    }
}
