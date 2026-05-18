package com.example.demo.model;

import java.math.BigDecimal;

public class MenuItem {
    private Integer menuItemId;
    private Integer categoryId;
    private String categoryName; // THÊM TRƯỜNG NÀY ĐỂ HIỂN THỊ TÊN DANH MỤC
    private String itemName;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isAvailable;

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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}
