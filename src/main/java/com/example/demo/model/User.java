package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer userID;
    private Integer accountID;
    private String userType;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private Integer rewardPoints;
    private String customerTier;
    private String notes;
}
