package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Services.DiningTableService;
import com.example.demo.model.DiningTable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "http://localhost:5173")
public class DiningTableController {

    @Autowired
    private DiningTableService tableService;

    @GetMapping
    public List<DiningTable> getAllTables() { return tableService.getAllTables(); }

    @GetMapping("/{id}")
    public ResponseEntity<DiningTable> getTableById(@PathVariable Integer id) {
        return tableService.getTableById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DiningTable createTable(@RequestBody DiningTable table) { return tableService.createTable(table); }

    @PutMapping("/{id}")
    public ResponseEntity<DiningTable> updateTable(@PathVariable Integer id, @RequestBody DiningTable table) {
        try {
            return ResponseEntity.ok(tableService.updateTable(id, table));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTable(@PathVariable Integer id) {
        try {
            tableService.deleteTable(id);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Không thể xóa bàn đang có hóa đơn/đặt bàn.");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{tableId}/status")
    public ResponseEntity<?> updateTableStatus(
            @PathVariable Integer tableId,
            @RequestParam String status) {
        try {
            DiningTable table = tableService.updateTableStatus(tableId, status);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Table status updated successfully",
                    "table", table
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to update table status: " + e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
