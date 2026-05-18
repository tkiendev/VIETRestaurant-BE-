package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.example.demo.model.DiningTable;
import com.example.demo.model.Area;
import com.example.demo.model.Floor;
import com.example.demo.repository.DiningTableRepository;
import com.example.demo.repository.AreaRepository;
import com.example.demo.repository.FloorRepository;
import com.example.demo.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DiningTableService {

    @Autowired
    private DiningTableRepository tableRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SessionManager sessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<DiningTable> getAllTables() { 
        return tableRepository.findAll(); 
    }

    public Optional<DiningTable> getTableById(Integer id) { 
        return tableRepository.findById(id); 
    }

    public DiningTable createTable(DiningTable table) { 
        // Đảm bảo có AreaID khi tạo bàn
        if (table.getAreaId() == null) {
            throw new IllegalArgumentException("Bàn ăn phải thuộc về một Khu vực (AreaID không được null)");
        }
        return tableRepository.save(table); 
    }

    public DiningTable updateTable(Integer id, DiningTable table) {
        if (table.getAreaId() == null) {
            throw new IllegalArgumentException("Bàn ăn phải thuộc về một Khu vực (AreaID không được null)");
        }
        tableRepository.update(id, table);
        table.setTableId(id);
        return table;
    }

    public void deleteTable(Integer id) {
        int rowsAffected = tableRepository.deleteById(id);
        if (rowsAffected == 0) throw new RuntimeException("Không tìm thấy bàn ăn");
    }

    public DiningTable updateTableStatus(Integer tableId, String newStatus) throws IOException {
        Optional<DiningTable> tableOpt = tableRepository.findById(tableId);
        if (!tableOpt.isPresent()) {
            throw new IllegalArgumentException("Table not found: " + tableId);
        }

        DiningTable table = tableOpt.get();
        String oldStatus = table.getStatus();

        table.setStatus(newStatus);
        tableRepository.update(tableId, table);

        broadcastTableStatusChange(table, oldStatus, newStatus);
        return table;
    }

    private void broadcastTableStatusChange(DiningTable table, String oldStatus, String newStatus) throws IOException {
        Optional<Area> areaOpt = areaRepository.findById(table.getAreaId());
        Area area = areaOpt.orElse(null);

        Optional<Floor> floorOpt = area != null ? floorRepository.findById(area.getFloorId()) : Optional.empty();
        Floor floor = floorOpt.orElse(null);

        Map<String, Object> statusEvent = new HashMap<>();
        statusEvent.put("event", "table_status_changed");
        statusEvent.put("tableId", table.getTableId());
        statusEvent.put("tableName", table.getTableName());
        statusEvent.put("oldStatus", oldStatus);
        statusEvent.put("newStatus", newStatus);
        statusEvent.put("capacity", table.getCapacity());
        statusEvent.put("areaId", table.getAreaId());
        statusEvent.put("areaName", area != null ? area.getAreaName() : "");
        statusEvent.put("floorId", area != null ? area.getFloorId() : null);
        statusEvent.put("floorName", floor != null ? floor.getFloorName() : "");
        statusEvent.put("timestamp", LocalDateTime.now().toString());

        sessionManager.broadcastToAll(statusEvent);
    }
}
