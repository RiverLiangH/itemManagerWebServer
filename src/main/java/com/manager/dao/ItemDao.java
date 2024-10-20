package com.manager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.manager.model.Item;
import com.manager.dao.DatabaseConnection;
import com.manager.model.User;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;
import org.json.JSONArray;

public class ItemDao {
    /*
    *  API Des: Add Item
    *  Author: hliang
    */
    public void insertItem(Item item) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO item (name, type, location, current_condition) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getType());
            stmt.setInt(3, item.getLocation());
            stmt.setInt(4, 1);

            stmt.executeUpdate();  // This executes the SQL statement
        }catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to insert item", e);
        }
    }

    public void deleteItem(int itemId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "DELETE FROM item WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);

            int rowsAffected = stmt.executeUpdate();  // 执行删除操作
            if (rowsAffected == 0) {
                throw new SQLException("No item found with the specified ID: " + itemId);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("No item found with the specified ID")) {
                throw e;  // 直接抛出该错误，保留原错误信息
            } else {
                e.printStackTrace();
                throw new SQLException("Failed to delete item with ID: " + itemId, e);
            }
        }
    }

    public JSONArray getAllItems() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT name, type, COUNT(*) AS item_total_count, " +
                "SUM(CASE WHEN current_condition = 1 THEN 1 ELSE 0 END) AS item_current_stock, " +
                "location " +
                "FROM item " +
                "GROUP BY name, type, location";

        JSONArray itemsArray = new JSONArray();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // 如果有数据，逐条遍历结果集
            while (rs.next()) {
                JSONObject itemObject = new JSONObject();
                itemObject.put("item_name", rs.getString("name"));
                itemObject.put("item_type", rs.getString("type"));
                itemObject.put("item_total_count", rs.getInt("item_total_count"));
                itemObject.put("item_current_stock", rs.getInt("item_current_stock"));
                itemObject.put("location", rs.getInt("location"));

                itemsArray.put(itemObject);
            }

            // 打印返回的数组大小，用于调试
            System.out.println("Total items fetched: " + itemsArray.length());

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Exception: " + e.getMessage());
            throw new SQLException("Failed to fetch items", e);
        }

        return itemsArray;
    }
}
