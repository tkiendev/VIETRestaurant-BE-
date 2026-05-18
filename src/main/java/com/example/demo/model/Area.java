package com.example.demo.model;

public class Area {
    private Integer areaId;
    private Integer floorId;
    private String floorName; // Lấy qua JOIN
    private String areaName;
    private String description;

    public Area() {}

    // Getters & Setters
    public Integer getAreaId() { return areaId; }
    public void setAreaId(Integer areaId) { this.areaId = areaId; }

    public Integer getFloorId() { return floorId; }
    public void setFloorId(Integer floorId) { this.floorId = floorId; }

    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }

    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
