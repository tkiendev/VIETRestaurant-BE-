package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.MenuItem;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.MenuItemIngredientRepository;
import com.example.demo.repository.IngredientRepository;
import com.example.demo.model.Ingredient;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuItemIngredientRepository menuItemIngredientRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    public List<java.util.Map<String, Object>> getMenuItemIngredients(Integer menuItemId) {
        String sql = "SELECT mii.IngredientID AS ingredientId, i.Name AS name, i.Unit AS unit, " +
                     "mii.Quantity AS quantity, COALESCE(ilc.UnitCost, i.DefaultUnitCost) AS unitCost " +
                     "FROM MenuItemIngredient mii " +
                     "JOIN Ingredient i ON mii.IngredientID = i.IngredientID " +
                     "LEFT JOIN IngredientLatestCost ilc ON ilc.IngredientID = mii.IngredientID " +
                     "WHERE mii.MenuItemID = ?";
        return menuItemRepository.getJdbcTemplate().query(sql, (rs, rowNum) -> {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("ingredientId", rs.getInt("ingredientId"));
            m.put("name", rs.getString("name"));
            m.put("unit", rs.getString("unit"));
            m.put("quantity", rs.getBigDecimal("quantity"));
            m.put("unitCost", rs.getBigDecimal("unitCost"));
            return m;
        }, menuItemId);
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = menuItemRepository.findAll();
        for (MenuItem item : items) {
            item.setIngredients(getMenuItemIngredients(item.getMenuItemId()));
        }
        return items;
    }

    public Optional<MenuItem> getMenuItemById(Integer id) {
        Optional<MenuItem> opt = menuItemRepository.findById(id);
        opt.ifPresent(item -> item.setIngredients(getMenuItemIngredients(item.getMenuItemId())));
        return opt;
    }

    public MenuItem createMenuItem(MenuItem menuItem) {
        MenuItem created = menuItemRepository.save(menuItem);

        // Nếu payload có danh sách nguyên liệu, lưu vào MenuItemIngredient
        if (menuItem.getIngredients() != null) {
            // xóa trước các liên kết cũ (nếu có)
            menuItemIngredientRepository.deleteByMenuItemId(created.getMenuItemId());
            for (var map : menuItem.getIngredients()) {
                // Expect keys: ingredientId or name, quantity, unitCost (optional)
                Integer ingredientId = map.get("ingredientId") != null ? ((Number) map.get("ingredientId")).intValue() : null;
                String name = map.get("name") != null ? map.get("name").toString() : null;
                java.math.BigDecimal quantity = map.get("quantity") != null ? new java.math.BigDecimal(map.get("quantity").toString()) : java.math.BigDecimal.ZERO;
                java.math.BigDecimal unitCost = map.get("unitCost") != null ? new java.math.BigDecimal(map.get("unitCost").toString()) : null;

                if (ingredientId == null && name != null) {
                    Ingredient ing = new Ingredient();
                    ing.setName(name);
                    ing.setUnit(map.get("unit") != null ? map.get("unit").toString() : "pcs");
                    ing.setDefaultUnitCost(unitCost != null ? unitCost : java.math.BigDecimal.ZERO);
                    ingredientRepository.save(ing);
                    ingredientId = ing.getIngredientId();
                }

                if (ingredientId != null) {
                    menuItemIngredientRepository.save(created.getMenuItemId(), ingredientId, quantity);
                    // Optionally set default cost if provided
                    if (unitCost != null) {
                        ingredientRepository.incrementStock(ingredientId, java.math.BigDecimal.ZERO);
                        jdbcUpdateSetDefaultCost(ingredientId, unitCost);
                    }
                }
            }
            created.setIngredients(getMenuItemIngredients(created.getMenuItemId()));
        }

        return created;
    }

    private void jdbcUpdateSetDefaultCost(Integer ingredientId, java.math.BigDecimal unitCost) {
        String sql = "UPDATE Ingredient SET DefaultUnitCost = ? WHERE IngredientID = ?";
        menuItemRepository.getJdbcTemplate().update(sql, unitCost, ingredientId);
    }

    public MenuItem updateMenuItem(Integer id, MenuItem menuItemDetails) {
        // Kiểm tra xem món ăn có tồn tại không
        Optional<MenuItem> existing = menuItemRepository.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }
        
        // Thực hiện update
        menuItemRepository.update(id, menuItemDetails);
        
        // Cập nhật nguyên liệu
        if (menuItemDetails.getIngredients() != null) {
            menuItemIngredientRepository.deleteByMenuItemId(id);
            for (var map : menuItemDetails.getIngredients()) {
                Integer ingredientId = map.get("ingredientId") != null ? ((Number) map.get("ingredientId")).intValue() : null;
                String name = map.get("name") != null ? map.get("name").toString() : null;
                java.math.BigDecimal quantity = map.get("quantity") != null ? new java.math.BigDecimal(map.get("quantity").toString()) : java.math.BigDecimal.ZERO;
                java.math.BigDecimal unitCost = map.get("unitCost") != null ? new java.math.BigDecimal(map.get("unitCost").toString()) : null;

                if (ingredientId == null && name != null) {
                    Ingredient ing = new Ingredient();
                    ing.setName(name);
                    ing.setUnit(map.get("unit") != null ? map.get("unit").toString() : "pcs");
                    ing.setDefaultUnitCost(unitCost != null ? unitCost : java.math.BigDecimal.ZERO);
                    ingredientRepository.save(ing);
                    ingredientId = ing.getIngredientId();
                }

                if (ingredientId != null) {
                    menuItemIngredientRepository.save(id, ingredientId, quantity);
                    if (unitCost != null) {
                        ingredientRepository.incrementStock(ingredientId, java.math.BigDecimal.ZERO);
                        jdbcUpdateSetDefaultCost(ingredientId, unitCost);
                    }
                }
            }
        }
        
        // Trả về dữ liệu mới
        menuItemDetails.setMenuItemId(id);
        menuItemDetails.setIngredients(getMenuItemIngredients(id));
        return menuItemDetails;
    }

    public void deleteMenuItem(Integer id) {
        int rowsAffected = menuItemRepository.deleteById(id);
        if (rowsAffected == 0) {
            throw new RuntimeException("Không thể xóa: ID không tồn tại.");
        }
    }
}
