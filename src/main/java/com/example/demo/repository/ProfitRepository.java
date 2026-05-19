package com.example.demo.repository;

import com.example.demo.model.MenuItemProfit;
import com.example.demo.model.SalesProfit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProfitRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<MenuItemProfit> menuItemProfitMapper = (rs, rowNum) -> mapMenuItemProfit(rs);

    private final RowMapper<SalesProfit> salesProfitMapper = (rs, rowNum) -> {
        SalesProfit s = new SalesProfit();
        s.setMenuItemId(rs.getInt("MenuItemID"));
        s.setItemName(rs.getString("ItemName"));
        s.setSoldQty(rs.getBigDecimal("SoldQty") != null ? rs.getBigDecimal("SoldQty") : BigDecimal.ZERO);
        s.setTotalProfit(rs.getBigDecimal("TotalProfit") != null ? rs.getBigDecimal("TotalProfit") : BigDecimal.ZERO);
        return s;
    };

    private MenuItemProfit mapMenuItemProfit(ResultSet rs) throws SQLException {
        MenuItemProfit m = new MenuItemProfit();
        m.setMenuItemId(rs.getInt("MenuItemID"));
        m.setItemName(rs.getString("ItemName"));
        m.setCostPrice(rs.getBigDecimal("CostPrice"));
        m.setSalePrice(rs.getBigDecimal("SalePrice"));
        m.setProfit(rs.getBigDecimal("Profit"));
        m.setProfitMarginPercent(rs.getBigDecimal("ProfitMarginPercent"));
        return m;
    }

    public List<MenuItemProfit> findMenuItemProfits() {
        String sql = "SELECT MenuItemID, ItemName, CostPrice, SalePrice, Profit, ProfitMarginPercent FROM MenuItemProfit";
        return jdbcTemplate.query(sql, menuItemProfitMapper);
    }

    public List<SalesProfit> findSalesProfitSummary() {
        String sql = "SELECT mi.MenuItemID AS MenuItemID, mi.ItemName AS ItemName, " +
                     "SUM(bd.Quantity) AS SoldQty, " +
                     "SUM(bd.Quantity * (bd.UnitPrice - COALESCE(mc.Cost,0))) AS TotalProfit " +
                     "FROM BillDetail bd " +
                     "JOIN MenuItem mi ON mi.MenuItemID = bd.MenuItemID " +
                     "LEFT JOIN MenuItemCost mc ON mc.MenuItemID = mi.MenuItemID " +
                     "GROUP BY mi.MenuItemID, mi.ItemName " +
                     "ORDER BY TotalProfit DESC";
        return jdbcTemplate.query(sql, salesProfitMapper);
    }
}
