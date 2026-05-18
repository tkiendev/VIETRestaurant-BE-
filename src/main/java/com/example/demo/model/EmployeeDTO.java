package com.example.demo.model;

import lombok.Data;

@Data
public class EmployeeDTO {
    // Account info
    private String username;
    private String password;
    private Integer roleID;
    
    // User info
    private String fullName;
    private String phone;
    private String email;
    private String address;
}
