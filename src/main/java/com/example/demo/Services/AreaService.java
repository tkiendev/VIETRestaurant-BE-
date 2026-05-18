package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.Area;
import com.example.demo.repository.AreaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AreaService {
    
    @Autowired
    private AreaRepository areaRepository;

    public List<Area> getAllAreas() { 
        return areaRepository.findAll(); 
    }


    public Optional<Area> getAreaById(Integer id) { 
        return areaRepository.findById(id); 
    }

    public Area createArea(Area area) { 
        // Đảm bảo có FloorID khi tạo khu vực
        if (area.getFloorId() == null) {
            throw new IllegalArgumentException("Khu vực phải thuộc về một Tầng (FloorID không được null)");
        }
        return areaRepository.save(area); 
    }

    public Area updateArea(Integer id, Area area) {
        if (area.getFloorId() == null) {
            throw new IllegalArgumentException("Khu vực phải thuộc về một Tầng (FloorID không được null)");
        }
        areaRepository.update(id, area);
        area.setAreaId(id);
        return area;
    }

    public void deleteArea(Integer id) {
        int rowsAffected = areaRepository.deleteById(id);
        if (rowsAffected == 0) throw new RuntimeException("Không tìm thấy khu vực");
    }
}