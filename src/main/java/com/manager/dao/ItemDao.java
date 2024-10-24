package com.manager.dao;

import java.sql.*;

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

import java.util.Random;

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

    public JSONArray getAllItemRecords() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM item";

        JSONArray itemsArray = new JSONArray();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // 如果有数据，逐条遍历结果集
            while (rs.next()) {
                JSONObject itemObject = new JSONObject();
                itemObject.put("item_id", rs.getInt("id"));
                itemObject.put("item_name", rs.getString("name"));
                itemObject.put("item_type", rs.getString("type"));
                itemObject.put("item_current_condition", rs.getInt("current_condition"));
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


    // 更新物品的状态：current_condition 列
    public void updateItemCondition(int itemId, int condition) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "UPDATE item SET current_condition = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, condition);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to update item condition", e);
        }
    }
    // 查询符合名称且状态为可借用(1)的物品，并随机选择一个
    public Item getAvailableItemsByName(String name) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM item WHERE name = ? AND current_condition = 1"; // 查询状态为1的可借用物品
        List<Item> availableItems = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name); // 设置物品名称
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // 将结果存储在Item对象中
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setType(rs.getString("type"));
                item.setCondition(rs.getInt("current_condition"));
                item.setLocation(rs.getInt("location"));

                availableItems.add(item);
            }
        }

        // 如果有符合条件的物品，随机选一个
        if (!availableItems.isEmpty()) {
            Random random = new Random();
            return availableItems.get(random.nextInt(availableItems.size()));
        }

        // 没有找到可借用的物品时返回null
        return null;
    }

    
}
