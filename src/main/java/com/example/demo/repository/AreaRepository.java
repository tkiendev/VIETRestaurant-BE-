package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Area;


import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class AreaRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Area> rowMapper = (rs, rowNum) -> {
        Area area = new Area();
        area.setAreaId(rs.getInt("AreaID"));
        area.setFloorId(rs.getObject("FloorID", Integer.class));
        area.setFloorName(rs.getString("FloorName"));
        area.setAreaName(rs.getString("AreaName"));
        area.setDescription(rs.getString("Description"));
        return area;
    };

    public List<Area> findAll() {
        String sql = "SELECT a.*, f.FloorName FROM Area a LEFT JOIN Floor f ON a.FloorID = f.FloorID";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Area save(Area area) {
        String sql = "INSERT INTO Area (FloorID, AreaName, Description) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, area.getFloorId());
            ps.setString(2, area.getAreaName());
            ps.setString(3, area.getDescription());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) area.setAreaId(keyHolder.getKey().intValue());
        return area;
    }

    public int update(Integer id, Area area) {
        String sql = "UPDATE Area SET FloorID = ?, AreaName = ?, Description = ? WHERE AreaID = ?";
        return jdbcTemplate.update(sql, area.getFloorId(), area.getAreaName(), area.getDescription(), id);
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM Area WHERE AreaID = ?", id);
    }

    public Optional<Area> findById(Integer id) {
        String sql = "SELECT a.*, f.FloorName " +
                     "FROM Area a " +
                     "LEFT JOIN Floor f ON a.FloorID = f.FloorID " +
                     "WHERE a.AreaID = ?";
                     
        List<Area> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
