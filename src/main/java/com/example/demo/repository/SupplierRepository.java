package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class SupplierRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<java.util.Map<String,Object>> rowMapper = (rs, rowNum) -> {
        java.util.Map<String,Object> m = new java.util.HashMap<>();
        m.put("SupplierID", rs.getInt("SupplierID"));
        m.put("SupplierName", rs.getString("SupplierName"));
        m.put("ContactInfo", rs.getString("ContactInfo"));
        return m;
    };

    public Optional<java.util.Map<String,Object>> findByName(String name) {
        String sql = "SELECT * FROM Supplier WHERE SupplierName = ?";
        List<java.util.Map<String,Object>> r = jdbcTemplate.query(sql, rowMapper, name);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    public java.util.List<java.util.Map<String,Object>> findAll() {
        String sql = "SELECT * FROM Supplier";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public int save(String name, String contact) {
        String sql = "INSERT INTO Supplier (SupplierName, ContactInfo) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, contact);
            return ps;
        }, keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }
}
