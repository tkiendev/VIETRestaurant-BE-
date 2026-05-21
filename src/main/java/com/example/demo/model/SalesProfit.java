package com.example.demo.model;

import java.math.BigDecimal;

public class SalesProfit {
    private Integer menuItemId;
    private String itemName;
    private BigDecimal soldQty;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;

    public Integer getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Integer menuItemId) { this.menuItemId = menuItemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public BigDecimal getSoldQty() { return soldQty; }
    public void setSoldQty(BigDecimal soldQty) { this.soldQty = soldQty; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
