package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Services.AreaService;
import com.example.demo.model.Area;


import java.util.List;

@RestController
@RequestMapping("/api/areas")
@CrossOrigin(origins = "http://localhost:5173")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @GetMapping
    public List<Area> getAllAreas() { return areaService.getAllAreas(); }

    @GetMapping("/{id}")
    public ResponseEntity<Area> getAreaById(@PathVariable Integer id) {
        return areaService.getAreaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Area createArea(@RequestBody Area area) { return areaService.createArea(area); }

    @PutMapping("/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable Integer id, @RequestBody Area area) {
        try {
            return ResponseEntity.ok(areaService.updateArea(id, area));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArea(@PathVariable Integer id) {
        try {
            areaService.deleteArea(id);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Không thể xóa khu vực đang chứa bàn ăn.");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
