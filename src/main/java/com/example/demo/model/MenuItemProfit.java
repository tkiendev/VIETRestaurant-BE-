package com.example.demo.model;

import java.math.BigDecimal;

public class MenuItemProfit {
    private Integer menuItemId;
    private String itemName;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private BigDecimal profit;
    private BigDecimal profitMarginPercent;

    public Integer getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Integer menuItemId) { this.menuItemId = menuItemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public BigDecimal getProfit() { return profit; }
    public void setProfit(BigDecimal profit) { this.profit = profit; }

    public BigDecimal getProfitMarginPercent() { return profitMarginPercent; }
    public void setProfitMarginPercent(BigDecimal profitMarginPercent) { this.profitMarginPercent = profitMarginPercent; }
}
