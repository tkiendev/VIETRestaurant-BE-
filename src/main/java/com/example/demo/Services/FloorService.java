package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.Floor;
import com.example.demo.repository.FloorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FloorService {
    @Autowired
    private FloorRepository floorRepository;

    public List<Floor> getAllFloors() { return floorRepository.findAll(); }
    public Optional<Floor> getFloorById(Integer id) { return floorRepository.findById(id); }
    public Floor createFloor(Floor floor) { return floorRepository.save(floor); }
    public Floor updateFloor(Integer id, Floor floor) {
        floorRepository.update(id, floor);
        floor.setFloorId(id);
        return floor;
    }
    public void deleteFloor(Integer id) {
        if (floorRepository.deleteById(id) == 0) throw new RuntimeException("Lỗi xóa tầng");
    }
}
