package com.example.demo.repository;

import com.example.demo.model.MenuItemIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class MenuItemIngredientRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<MenuItemIngredient> rowMapper = (rs, rowNum) -> {
        MenuItemIngredient m = new MenuItemIngredient();
        m.setMenuItemIngredientId(rs.getInt("MenuItemIngredientID"));
        m.setMenuItemId(rs.getInt("MenuItemID"));
        m.setIngredientId(rs.getInt("IngredientID"));
        m.setQuantity(rs.getBigDecimal("Quantity"));
        return m;
    };

    public int save(Integer menuItemId, Integer ingredientId, BigDecimal quantity) {
        String sql = "INSERT INTO MenuItemIngredient (MenuItemID, IngredientID, Quantity) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, menuItemId);
            ps.setInt(2, ingredientId);
            ps.setBigDecimal(3, quantity);
            return ps;
        }, keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public List<MenuItemIngredient> findByMenuItemId(Integer menuItemId) {
        String sql = "SELECT * FROM MenuItemIngredient WHERE MenuItemID = ?";
        return jdbcTemplate.query(sql, rowMapper, menuItemId);
    }

    public int deleteByMenuItemId(Integer menuItemId) {
        return jdbcTemplate.update("DELETE FROM MenuItemIngredient WHERE MenuItemID = ?", menuItemId);
    }
}
