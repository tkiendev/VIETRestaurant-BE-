package com.example.demo.model;

import java.math.BigDecimal;

public class BillDetail {
    private Integer billDetailID;
    private Integer billID;
    private Integer menuItemID;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String specialNote;

    public BillDetail() {}

    public BillDetail(Integer billDetailID, Integer billID, Integer menuItemID, Integer quantity, BigDecimal unitPrice, String specialNote) {
        this.billDetailID = billDetailID;
        this.billID = billID;
        this.menuItemID = menuItemID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.specialNote = specialNote;
    }

    public Integer getBillDetailID() {
        return billDetailID;
    }

    public void setBillDetailID(Integer billDetailID) {
        this.billDetailID = billDetailID;
    }

    public Integer getBillID() {
        return billID;
    }

    public void setBillID(Integer billID) {
        this.billID = billID;
    }

    public Integer getMenuItemID() {
        return menuItemID;
    }

    public void setMenuItemID(Integer menuItemID) {
        this.menuItemID = menuItemID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSpecialNote() {
        return specialNote;
    }

    public void setSpecialNote(String specialNote) {
        this.specialNote = specialNote;
    }

    @Override
    public String toString() {
        return "BillDetail{" +
                "billDetailID=" + billDetailID +
                ", billID=" + billID +
                ", menuItemID=" + menuItemID +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", specialNote='" + specialNote + '\'' +
                '}';
    }
}
