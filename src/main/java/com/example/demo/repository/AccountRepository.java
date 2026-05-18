package com.example.demo.repository;

import com.example.demo.model.Account;
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
public class AccountRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Account> rowMapper = (rs, rowNum) -> {
        return new Account(
                rs.getInt("AccountID"),
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getInt("RoleID"),
                rs.getBoolean("IsActive")
        );
    };

    public Optional<Account> findByUsername(String username) {
        List<Account> accounts = jdbcTemplate.query("SELECT * FROM Account WHERE Username = ?", rowMapper, username);
        return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts.get(0));
    }

    public Optional<Account> findById(Integer id) {
        List<Account> accounts = jdbcTemplate.query("SELECT * FROM Account WHERE AccountID = ?", rowMapper, id);
        return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts.get(0));
    }

    public Account save(Account account) {
        String sql = "INSERT INTO Account (Username, Password, RoleID, IsActive) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            if (account.getRoleID() != null) {
                ps.setInt(3, account.getRoleID());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setBoolean(4, account.getIsActive() != null ? account.getIsActive() : true);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            account.setAccountID(keyHolder.getKey().intValue());
        }
        return account;
    }

    public int update(Integer id, Account account) {
        String sql = "UPDATE Account SET Username = ?, Password = ?, RoleID = ?, IsActive = ? WHERE AccountID = ?";
        return jdbcTemplate.update(sql,
                account.getUsername(),
                account.getPassword(),
                account.getRoleID(),
                account.getIsActive(),
                id);
    }
    
    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM Account WHERE AccountID = ?", id);
    }
}
