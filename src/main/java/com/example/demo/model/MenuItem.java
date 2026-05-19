package com.example.demo.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class MenuItem {
    private Integer menuItemId;
    private Integer categoryId;
    private String categoryName; // THÊM TRƯỜNG NÀY ĐỂ HIỂN THỊ TÊN DANH MỤC
    private String itemName;
    private BigDecimal price;
    private BigDecimal costPrice;
    private String imageUrl;
    private Boolean isAvailable;
    private List<Map<String,Object>> ingredients; // transient: {ingredientId, name, unit, quantity, unitCost}

    public MenuItem() {}

    // Getters and Setters
    public Integer getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Integer menuItemId) { this.menuItemId = menuItemId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public List<Map<String, Object>> getIngredients() { return ingredients; }
    public void setIngredients(List<Map<String, Object>> ingredients) { this.ingredients = ingredients; }
}
