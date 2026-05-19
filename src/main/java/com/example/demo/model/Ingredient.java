package com.example.demo.model;

import java.math.BigDecimal;

public class Ingredient {
    private Integer ingredientId;
    private String name;
    private String unit;
    private BigDecimal defaultUnitCost;
    private String notes;

    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getDefaultUnitCost() { return defaultUnitCost; }
    public void setDefaultUnitCost(BigDecimal defaultUnitCost) { this.defaultUnitCost = defaultUnitCost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
