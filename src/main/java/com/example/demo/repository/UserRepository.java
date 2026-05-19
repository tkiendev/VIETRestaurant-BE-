package com.example.demo.repository;

import com.example.demo.model.User;
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
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        return new User(
                rs.getInt("UserID"),
                rs.getObject("AccountID") != null ? rs.getInt("AccountID") : null,
                rs.getString("UserType"),
                rs.getString("FullName"),
                rs.getString("Phone"),
                rs.getString("Email"),
                rs.getString("Address"),
                rs.getInt("RewardPoints"),
                rs.getString("CustomerTier"),
                rs.getString("Notes")
        );
    };

    public List<User> findAllEmployees() {
        return jdbcTemplate.query("SELECT * FROM `User` WHERE UserType = 'Employee'", rowMapper);
    }

    public List<User> findAllCustomers() {
        return jdbcTemplate.query("SELECT * FROM `User` WHERE UserType = 'Customer' ORDER BY UserID DESC", rowMapper);
    }

    public Optional<User> findByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return Optional.empty();
        }
        List<User> users = jdbcTemplate.query("SELECT * FROM `User` WHERE Phone = ? LIMIT 1", rowMapper, phone.trim());
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<User> findById(Integer id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM `User` WHERE UserID = ?", rowMapper, id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }
    
    public Optional<User> findByAccountId(Integer accountId) {
        List<User> users = jdbcTemplate.query("SELECT * FROM `User` WHERE AccountID = ?", rowMapper, accountId);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public User save(User user) {
        String sql = "INSERT INTO `User` (AccountID, UserType, FullName, Phone, Email, Address, RewardPoints, CustomerTier, Notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (user.getAccountID() != null) {
                ps.setInt(1, user.getAccountID());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, user.getUserType() != null ? user.getUserType() : "Customer");
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getAddress());
            ps.setInt(7, user.getRewardPoints() != null ? user.getRewardPoints() : 0);
            ps.setString(8, user.getCustomerTier() != null ? user.getCustomerTier() : "Thường");
            ps.setString(9, user.getNotes());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setUserID(keyHolder.getKey().intValue());
        }
        return user;
    }

    public int update(Integer id, User user) {
        String sql = "UPDATE `User` SET FullName = ?, Phone = ?, Email = ?, Address = ? WHERE UserID = ?";
        return jdbcTemplate.update(sql,
                user.getFullName(),
                user.getPhone(),
                user.getEmail(),
                user.getAddress(),
                id);
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM `User` WHERE UserID = ?", id);
    }
}
