package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Floor;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class FloorRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Floor> rowMapper = (rs, rowNum) -> {
        Floor floor = new Floor();
        floor.setFloorId(rs.getInt("FloorID"));
        floor.setFloorName(rs.getString("FloorName"));
        floor.setDescription(rs.getString("Description"));
        return floor;
    };

    public List<Floor> findAll() {
        return jdbcTemplate.query("SELECT * FROM Floor", rowMapper);
    }

    public Optional<Floor> findById(Integer id) {
        String sql = "SELECT * FROM Floor WHERE FloorID = ?";
        List<Floor> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Floor save(Floor floor) {
        String sql = "INSERT INTO Floor (FloorName, Description) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, floor.getFloorName());
            ps.setString(2, floor.getDescription());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) floor.setFloorId(keyHolder.getKey().intValue());
        return floor;
    }

    public int update(Integer id, Floor floor) {
        String sql = "UPDATE Floor SET FloorName = ?, Description = ? WHERE FloorID = ?";
        return jdbcTemplate.update(sql, floor.getFloorName(), floor.getDescription(), id);
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM Floor WHERE FloorID = ?", id);
    }
}
