package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Category;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Ánh xạ dữ liệu từ ResultSet
    private final RowMapper<Category> rowMapper = (rs, rowNum) -> {
        Category category = new Category();
        category.setCategoryId(rs.getInt("CategoryID"));
        category.setCategoryName(rs.getString("CategoryName"));
        return category;
    };

    // 1. Lấy tất cả danh mục
    public List<Category> findAll() {
        String sql = "SELECT * FROM Category";
        return jdbcTemplate.query(sql, rowMapper);
    }

    // 2. Lấy danh mục theo ID
    public Optional<Category> findById(Integer id) {
        String sql = "SELECT * FROM Category WHERE CategoryID = ?";
        List<Category> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // 3. Thêm mới danh mục
    public Category save(Category category) {
        String sql = "INSERT INTO Category (CategoryName) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getCategoryName());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            category.setCategoryId(keyHolder.getKey().intValue());
        }
        return category;
    }

    // 4. Cập nhật danh mục
    public int update(Integer id, Category category) {
        String sql = "UPDATE Category SET CategoryName = ? WHERE CategoryID = ?";
        return jdbcTemplate.update(sql, category.getCategoryName(), id);
    }

    // 5. Xóa danh mục
    public int deleteById(Integer id) {
        String sql = "DELETE FROM Category WHERE CategoryID = ?";
        return jdbcTemplate.update(sql, id);
    }
}