package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class SchemaDumper {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void dumpSchema() {
        System.out.println("=== SCHEMA DUMP START ===");
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList("SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'quanlynhahang' AND TABLE_NAME IN ('Bill', 'BillDetail', 'KitchenOrder')");
            for (Map<String, Object> col : columns) {
                System.out.println(col.get("TABLE_NAME") + " -> " + col.get("COLUMN_NAME") + " (" + col.get("DATA_TYPE") + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=== SCHEMA DUMP END ===");
    }
}
