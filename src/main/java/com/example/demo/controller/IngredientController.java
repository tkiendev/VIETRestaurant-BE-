package com.example.demo.controller;

import com.example.demo.model.Ingredient;
import com.example.demo.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class IngredientController {

    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            String sql = "SELECT i.*, COALESCE(s.QuantityOnHand,0) AS QuantityOnHand FROM Ingredient i LEFT JOIN IngredientStock s ON i.IngredientID = s.IngredientID";
            var list = ingredientRepository.getJdbcTemplate().queryForList(sql);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Ingredient ing) {
        try {
            Ingredient created = ingredientRepository.save(ing);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
