package com.example.demo.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, Category categoryDetails) {
        Optional<Category> existing = categoryRepository.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("Không tìm thấy danh mục với ID: " + id);
        }

        if (categoryDetails.getCategoryName() == null || categoryDetails.getCategoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }

        categoryRepository.update(id, categoryDetails);
        categoryDetails.setCategoryId(id);
        return categoryDetails;
    }

    public void deleteCategory(Integer id) {
        // Lưu ý: Nếu có MenuItem đang dùng CategoryID này, việc xóa có thể bị lỗi do khóa ngoại (Foreign Key)
        // Bạn có thể cần xử lý try-catch ở Controller để trả về thông báo lỗi thân thiện
        int rowsAffected = categoryRepository.deleteById(id);
        if (rowsAffected == 0) {
            throw new RuntimeException("Không thể xóa: ID không tồn tại.");
        }
    }
}
