package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.demo.model.DiningTable;


import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DiningTableRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<DiningTable> rowMapper = (rs, rowNum) -> {
        DiningTable table = new DiningTable();
        table.setTableId(rs.getInt("TableID"));
        table.setAreaId(rs.getObject("AreaID", Integer.class));
        table.setAreaName(rs.getString("AreaName"));
        table.setFloorName(rs.getString("FloorName"));
        table.setTableName(rs.getString("TableName"));
        table.setCapacity(rs.getInt("Capacity"));
        table.setStatus(rs.getString("Status"));
        return table;
    };

    public List<DiningTable> findAll() {
        String sql = "SELECT t.*, a.AreaName, f.FloorName " +
                     "FROM DiningTable t " +
                     "LEFT JOIN Area a ON t.AreaID = a.AreaID " +
                     "LEFT JOIN Floor f ON a.FloorID = f.FloorID";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public DiningTable save(DiningTable table) {
        String sql = "INSERT INTO DiningTable (AreaID, TableName, Capacity, Status) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, table.getAreaId());
            ps.setString(2, table.getTableName());
            ps.setObject(3, table.getCapacity() != null ? table.getCapacity() : 4);
            ps.setString(4, table.getStatus() != null ? table.getStatus() : "Trống");
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) table.setTableId(keyHolder.getKey().intValue());
        return table;
    }

    public int update(Integer id, DiningTable table) {
        String sql = "UPDATE DiningTable SET AreaID = ?, TableName = ?, Capacity = ?, Status = ? WHERE TableID = ?";
        return jdbcTemplate.update(sql, table.getAreaId(), table.getTableName(), table.getCapacity(), table.getStatus(), id);
    }

    public int deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM DiningTable WHERE TableID = ?", id);
    }

    public Optional<DiningTable> findById(Integer id) {
        String sql = "SELECT t.*, a.AreaName, f.FloorName " +
                     "FROM DiningTable t " +
                     "LEFT JOIN Area a ON t.AreaID = a.AreaID " +
                     "LEFT JOIN Floor f ON a.FloorID = f.FloorID " +
                     "WHERE t.TableID = ?";
                     
        List<DiningTable> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
