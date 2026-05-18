package com.example.demo.model;

import java.time.LocalDateTime;

public class KitchenOrder {
    private Integer kitchenOrderID;
    private Integer billDetailID;
    private String status;
    private String specialNote;
    private LocalDateTime receivedTime;
    private LocalDateTime completedTime;

    public KitchenOrder() {}

    public KitchenOrder(Integer kitchenOrderID, Integer billDetailID, String status, String specialNote, LocalDateTime receivedTime, LocalDateTime completedTime) {
        this.kitchenOrderID = kitchenOrderID;
        this.billDetailID = billDetailID;
        this.status = status;
        this.specialNote = specialNote;
        this.receivedTime = receivedTime;
        this.completedTime = completedTime;
    }

    public Integer getKitchenOrderID() {
        return kitchenOrderID;
    }

    public void setKitchenOrderID(Integer kitchenOrderID) {
        this.kitchenOrderID = kitchenOrderID;
    }

    public Integer getBillDetailID() {
        return billDetailID;
    }

    public void setBillDetailID(Integer billDetailID) {
        this.billDetailID = billDetailID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecialNote() {
        return specialNote;
    }

    public void setSpecialNote(String specialNote) {
        this.specialNote = specialNote;
    }

    public LocalDateTime getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(LocalDateTime receivedTime) {
        this.receivedTime = receivedTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }

    @Override
    public String toString() {
        return "KitchenOrder{" +
                "kitchenOrderID=" + kitchenOrderID +
                ", billDetailID=" + billDetailID +
                ", status='" + status + '\'' +
                ", specialNote='" + specialNote + '\'' +
                ", receivedTime=" + receivedTime +
                ", completedTime=" + completedTime +
                '}';
    }
}
