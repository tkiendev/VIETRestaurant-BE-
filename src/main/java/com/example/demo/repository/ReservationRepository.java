package com.example.demo.repository;

import com.example.demo.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Reservation> rowMapper = (rs, rowNum) -> {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("ReservationID"));
        reservation.setCustomerId(rs.getObject("CustomerID", Integer.class));
        reservation.setTableId(rs.getObject("TableID", Integer.class));
        reservation.setReservationTime(rs.getTimestamp("ReservationTime").toLocalDateTime());
        reservation.setGuestCount(rs.getInt("GuestCount"));
        reservation.setStatus(rs.getString("Status"));
        try {
            reservation.setTableName(rs.getString("TableName"));
        } catch (Exception ignored) {
        }
        return reservation;
    };

    public List<Reservation> findAll() {
        String sql = "SELECT r.*, t.TableName FROM Reservation r LEFT JOIN DiningTable t ON r.TableID = t.TableID ORDER BY r.ReservationTime ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Reservation> findById(Integer id) {
        String sql = "SELECT r.*, t.TableName FROM Reservation r LEFT JOIN DiningTable t ON r.TableID = t.TableID WHERE r.ReservationID = ?";
        List<Reservation> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO Reservation (CustomerID, TableID, ReservationTime, GuestCount, Status) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, reservation.getCustomerId());
            ps.setObject(2, reservation.getTableId());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getReservationTime()));
            ps.setInt(4, reservation.getGuestCount());
            ps.setString(5, reservation.getStatus());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            reservation.setReservationId(keyHolder.getKey().intValue());
        }
        return reservation;
    }

    public List<Integer> findReservedTableIds(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT DISTINCT TableID FROM Reservation WHERE ReservationTime BETWEEN ? AND ? AND Status IN ('Chờ xác nhận', 'Đã xác nhận', 'Đã đến')";
        return jdbcTemplate.queryForList(sql, Integer.class, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }
}
