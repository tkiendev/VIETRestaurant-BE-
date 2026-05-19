package com.example.demo.model;

import java.math.BigDecimal;

public class MenuItemIngredient {
    private Integer menuItemIngredientId;
    private Integer menuItemId;
    private Integer ingredientId;
    private BigDecimal quantity;

    public Integer getMenuItemIngredientId() { return menuItemIngredientId; }
    public void setMenuItemIngredientId(Integer menuItemIngredientId) { this.menuItemIngredientId = menuItemIngredientId; }

    public Integer getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Integer menuItemId) { this.menuItemId = menuItemId; }

    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
