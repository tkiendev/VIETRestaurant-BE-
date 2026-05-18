package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.MenuItem;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class MenuItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Ánh xạ dữ liệu từ ResultSet sang Object MenuItem
    private final RowMapper<MenuItem> rowMapper = (rs, rowNum) -> {
        MenuItem item = new MenuItem();
        item.setMenuItemId(rs.getInt("MenuItemID"));
        item.setCategoryId(rs.getObject("CategoryID", Integer.class));
        
        // Map thêm Tên danh mục từ câu lệnh JOIN
        item.setCategoryName(rs.getString("CategoryName")); 
        
        item.setItemName(rs.getString("ItemName"));
        item.setPrice(rs.getBigDecimal("Price"));
        item.setImageUrl(rs.getString("ImageURL"));
        item.setIsAvailable(rs.getBoolean("IsAvailable"));
        return item;
    };

    // 1. Lấy danh sách (Có JOIN để lấy tên danh mục)
    public List<MenuItem> findAll() {
        String sql = "SELECT m.*, c.CategoryName " +
                     "FROM MenuItem m " +
                     "LEFT JOIN Category c ON m.CategoryID = c.CategoryID";
        return jdbcTemplate.query(sql, rowMapper);
    }

    // 2. Lấy chi tiết (Có JOIN để lấy tên danh mục)
    public Optional<MenuItem> findById(Integer id) {
        String sql = "SELECT m.*, c.CategoryName " +
                     "FROM MenuItem m " +
                     "LEFT JOIN Category c ON m.CategoryID = c.CategoryID " +
                     "WHERE m.MenuItemID = ?";
        List<MenuItem> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // 3. Thêm mới (Giữ nguyên)
    public MenuItem save(MenuItem item) {
        String sql = "INSERT INTO MenuItem (CategoryID, ItemName, Price, ImageURL, IsAvailable) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, item.getCategoryId());
            ps.setString(2, item.getItemName());
            ps.setBigDecimal(3, item.getPrice());
            ps.setString(4, item.getImageUrl());
            ps.setObject(5, item.getIsAvailable() != null ? item.getIsAvailable() : true);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            item.setMenuItemId(keyHolder.getKey().intValue());
        }
        return item;
    }

    // 4. Cập nhật (Giữ nguyên)
    public int update(Integer id, MenuItem item) {
        String sql = "UPDATE MenuItem SET CategoryID = ?, ItemName = ?, Price = ?, ImageURL = ?, IsAvailable = ? WHERE MenuItemID = ?";
        return jdbcTemplate.update(sql, 
                item.getCategoryId(), 
                item.getItemName(), 
                item.getPrice(), 
                item.getImageUrl(), 
                item.getIsAvailable(), 
                id);
    }

    // 5. Xóa (Giữ nguyên)
    public int deleteById(Integer id) {
        String sql = "DELETE FROM MenuItem WHERE MenuItemID = ?";
        return jdbcTemplate.update(sql, id);
    }
}