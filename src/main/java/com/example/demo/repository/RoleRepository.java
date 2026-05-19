package com.example.demo.repository;

import com.example.demo.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Role> rowMapper = (rs, rowNum) -> {
        return new Role(
                rs.getInt("RoleID"),
                rs.getString("RoleName"),
                rs.getString("Description")
        );
    };

    public List<Role> findAll() {
        return jdbcTemplate.query("SELECT * FROM `Role`", rowMapper);
    }

    public Optional<Role> findById(Integer id) {
        List<Role> roles = jdbcTemplate.query("SELECT * FROM `Role` WHERE RoleID = ?", rowMapper, id);
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }
}
