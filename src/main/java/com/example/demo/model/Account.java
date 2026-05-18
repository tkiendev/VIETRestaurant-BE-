package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Integer accountID;
    private String username;
    private String password;
    private Integer roleID;
    private Boolean isActive;
}
