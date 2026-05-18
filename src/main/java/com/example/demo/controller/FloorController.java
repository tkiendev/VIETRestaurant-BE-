package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Services.FloorService;
import com.example.demo.model.Floor;

import java.util.List;

@RestController
@RequestMapping("/api/floors")
@CrossOrigin(origins = "http://localhost:5173")
public class FloorController {
    @Autowired
    private FloorService floorService;

    @GetMapping
    public List<Floor> getAllFloors() { return floorService.getAllFloors(); }

    @PostMapping
    public Floor createFloor(@RequestBody Floor floor) { return floorService.createFloor(floor); }

    @PutMapping("/{id}")
    public ResponseEntity<Floor> updateFloor(@PathVariable Integer id, @RequestBody Floor floor) {
        return ResponseEntity.ok(floorService.updateFloor(id, floor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFloor(@PathVariable Integer id) {
        try {
            floorService.deleteFloor(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể xóa tầng đang có khu vực.");
        }
    }
}
