package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private Integer billID;
    private Integer tableID;
    private Integer customerID;
    private Integer cashierID;
    private BigDecimal totalAmount;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;
    private String status;

    public Bill() {}

    public Bill(Integer billID, Integer tableID, Integer customerID, Integer cashierID, BigDecimal totalAmount, LocalDateTime timeIn, LocalDateTime timeOut, String status) {
        this.billID = billID;
        this.tableID = tableID;
        this.customerID = customerID;
        this.cashierID = cashierID;
        this.totalAmount = totalAmount;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.status = status;
    }

    public Integer getBillID() {
        return billID;
    }

    public void setBillID(Integer billID) {
        this.billID = billID;
    }

    public Integer getTableID() {
        return tableID;
    }

    public void setTableID(Integer tableID) {
        this.tableID = tableID;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public Integer getCashierID() {
        return cashierID;
    }

    public void setCashierID(Integer cashierID) {
        this.cashierID = cashierID;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalDateTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalDateTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalDateTime timeOut) {
        this.timeOut = timeOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billID=" + billID +
                ", tableID=" + tableID +
                ", customerID=" + customerID +
                ", cashierID=" + cashierID +
                ", totalAmount=" + totalAmount +
                ", timeIn=" + timeIn +
                ", timeOut=" + timeOut +
                ", status='" + status + '\'' +
                '}';
    }
}
