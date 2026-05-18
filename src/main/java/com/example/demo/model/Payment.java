package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Integer paymentId;
    private Integer billID;
    private String paymentMethod;
    private BigDecimal amountPaid;
    private LocalDateTime paymentTime;

    public Payment() {}

    public Payment(Integer paymentId, Integer billID, String paymentMethod, BigDecimal amountPaid, LocalDateTime paymentTime) {
        this.paymentId = paymentId;
        this.billID = billID;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.paymentTime = paymentTime;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getBillID() {
        return billID;
    }

    public void setBillID(Integer billID) {
        this.billID = billID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", billID=" + billID +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", amountPaid=" + amountPaid +
                ", paymentTime=" + paymentTime +
                '}';
    }
}
