package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.MenuItem;
import com.example.demo.repository.MenuItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public Optional<MenuItem> getMenuItemById(Integer id) {
        return menuItemRepository.findById(id);
    }

    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public MenuItem updateMenuItem(Integer id, MenuItem menuItemDetails) {
        // Kiểm tra xem món ăn có tồn tại không
        Optional<MenuItem> existing = menuItemRepository.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }
        
        // Thực hiện update
        menuItemRepository.update(id, menuItemDetails);
        
        // Trả về dữ liệu mới
        menuItemDetails.setMenuItemId(id);
        return menuItemDetails;
    }

    public void deleteMenuItem(Integer id) {
        int rowsAffected = menuItemRepository.deleteById(id);
        if (rowsAffected == 0) {
            throw new RuntimeException("Không thể xóa: ID không tồn tại.");
        }
    }
}
