package com.example.demo.controller;

import com.example.demo.model.DiningTable;
import com.example.demo.repository.DiningTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    @Autowired
    private DiningTableRepository diningTableRepository;

    @GetMapping("/tables")
    public ResponseEntity<?> getAllTablesStatus() {
        try {
            List<DiningTable> tables = diningTableRepository.findAll();
            return ResponseEntity.ok(new DashboardResponse(tables));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ErrorResponse("Failed to fetch tables: " + e.getMessage()));
        }
    }

    @GetMapping("/tables/summary")
    public ResponseEntity<?> getTablesSummary() {
        try {
            List<DiningTable> tables = diningTableRepository.findAll();
            int total = tables.size();
            long empty = tables.stream().filter(t -> "Trống".equals(t.getStatus())).count();
            long inService = tables.stream().filter(t -> "Đang phục vụ".equals(t.getStatus())).count();
            long maintenance = tables.stream().filter(t -> "Bảo trì".equals(t.getStatus())).count();

            return ResponseEntity.ok(new TablesSummary(total, (int) empty, (int) inService, (int) maintenance, tables));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ErrorResponse("Failed to fetch summary: " + e.getMessage()));
        }
    }

    static class DashboardResponse {
        public List<DiningTable> tables;
        public long timestamp;

        public DashboardResponse(List<DiningTable> tables) {
            this.tables = tables;
            this.timestamp = System.currentTimeMillis();
        }
    }

    static class TablesSummary {
        public int totalTables;
        public int emptyTables;
        public int inServiceTables;
        public int maintenanceTables;
        public List<DiningTable> tables;
        public long timestamp;

        public TablesSummary(int total, int empty, int inService, int maintenance, List<DiningTable> tables) {
            this.totalTables = total;
            this.emptyTables = empty;
            this.inServiceTables = inService;
            this.maintenanceTables = maintenance;
            this.tables = tables;
            this.timestamp = System.currentTimeMillis();
        }
    }

    static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
