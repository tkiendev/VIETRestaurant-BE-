package com.example.demo.repository;

import com.example.demo.model.Ingredient;
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
import java.util.Optional;

@Repository
public class IngredientRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() { return this.jdbcTemplate; }

    private final RowMapper<Ingredient> rowMapper = (rs, rowNum) -> {
        Ingredient ing = new Ingredient();
        ing.setIngredientId(rs.getInt("IngredientID"));
        ing.setName(rs.getString("Name"));
        ing.setUnit(rs.getString("Unit"));
        ing.setDefaultUnitCost(rs.getBigDecimal("DefaultUnitCost"));
        ing.setNotes(rs.getString("Notes"));
        return ing;
    };

    public Optional<Ingredient> findById(Integer id) {
        String sql = "SELECT * FROM Ingredient WHERE IngredientID = ?";
        List<Ingredient> r = jdbcTemplate.query(sql, rowMapper, id);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    public Optional<Ingredient> findByName(String name) {
        String sql = "SELECT * FROM Ingredient WHERE Name = ?";
        List<Ingredient> r = jdbcTemplate.query(sql, rowMapper, name);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    public Ingredient save(Ingredient ing) {
        String sql = "INSERT INTO Ingredient (Name, Unit, DefaultUnitCost, Notes) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ing.getName());
            ps.setString(2, ing.getUnit());
            ps.setBigDecimal(3, ing.getDefaultUnitCost() != null ? ing.getDefaultUnitCost() : BigDecimal.ZERO);
            ps.setString(4, ing.getNotes());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) ing.setIngredientId(keyHolder.getKey().intValue());
        // Ensure IngredientStock row exists
        jdbcTemplate.update("INSERT IGNORE INTO IngredientStock (IngredientID, QuantityOnHand) VALUES (?, 0)", ing.getIngredientId());
        return ing;
    }

    public int incrementStock(Integer ingredientId, BigDecimal qty) {
        String select = "SELECT COUNT(*) FROM IngredientStock WHERE IngredientID = ?";
        Integer count = jdbcTemplate.queryForObject(select, Integer.class, ingredientId);
        if (count == null || count == 0) {
            jdbcTemplate.update("INSERT INTO IngredientStock (IngredientID, QuantityOnHand) VALUES (?, ?)", ingredientId, qty);
            return 1;
        }
        return jdbcTemplate.update("UPDATE IngredientStock SET QuantityOnHand = QuantityOnHand + ? WHERE IngredientID = ?", qty, ingredientId);
    }

    public int decrementStock(Integer ingredientId, BigDecimal qty) {
        return jdbcTemplate.update("UPDATE IngredientStock SET QuantityOnHand = QuantityOnHand - ? WHERE IngredientID = ?", qty, ingredientId);
    }

    public BigDecimal getStock(Integer ingredientId) {
        String sql = "SELECT QuantityOnHand FROM IngredientStock WHERE IngredientID = ?";
        try {
            BigDecimal q = jdbcTemplate.queryForObject(sql, BigDecimal.class, ingredientId);
            return q != null ? q : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
