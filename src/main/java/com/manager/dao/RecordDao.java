package com.manager.dao;

import com.manager.model.UserBorrowRecord;
import com.manager.dao.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordDao {
    
    // 插入借物记录
    public void insertRecord(UserBorrowRecord record) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO user_borrow_record (user_id, item_id, borrow_time) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, record.getUserId());
            stmt.setInt(2, record.getItemId());
            stmt.setTime(3, record.getBorrowTime());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to insert record: " + e.getMessage(), e);
        }
    }

    // 更新归还时间
    public void updateReturnTime(int recordId, Timestamp returnTime) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "UPDATE user_borrow_record SET return_time = ? WHERE id = ?";
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, returnTime);
            stmt.setInt(2, recordId);
    
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated); // 打印受影响的行数
        } catch (SQLException e) {
            throw new SQLException("Error updating return time", e);
        }
    }
    
    

    // 查询用户的所有借物记录
    public List<UserBorrowRecord> getRecordsByUserId(int userId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM user_borrow_record WHERE user_id = ?";
        List<UserBorrowRecord> records = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UserBorrowRecord record = new UserBorrowRecord();
                record.setId(rs.getInt("id"));
                record.setUserId(rs.getInt("user_id"));
                record.setItemId(rs.getInt("item_id"));
                record.setBorrowTime(rs.getTime("borrow_time"));
                record.setReturnTime(rs.getTime("return_time"));

                records.add(record);
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching records", e);
        }

        return records;
    }

    // 根据记录 ID 查询借物记录
    public UserBorrowRecord getRecordById(int recordId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM user_borrow_record WHERE id = ?";
        UserBorrowRecord record = null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recordId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                record = new UserBorrowRecord();
                record.setId(rs.getInt("id"));
                record.setUserId(rs.getInt("user_id"));
                record.setItemId(rs.getInt("item_id"));
                record.setBorrowTime(rs.getTime("borrow_time"));
                record.setReturnTime(rs.getTime("return_time"));
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching record by ID", e);
        }

        return record;
    }
    // 检查某用户是否已经借用了该物品且未归还
    public boolean isItemBorrowed(int userId, int itemId) throws SQLException {
        String query = "SELECT * FROM user_borrow_record WHERE user_id = ? AND item_id = ? AND return_time IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, itemId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // 如果有结果，表示已经借用
        }
    }

    // 检查某物品是否已经归还
    public boolean isItemReturned(int recordId) throws SQLException {
        String query = "SELECT * FROM user_borrow_record WHERE id = ? AND return_time IS NOT NULL";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recordId);
            ResultSet rs = stmt.executeQuery();
            
            boolean isReturned = rs.next();  // 只调用一次 rs.next()
            
            if (isReturned) {
                System.out.println("Item already returned for record ID: " + recordId);  // 添加日志检查
            } else {
                System.out.println("Item has not been returned yet for record ID: " + recordId);  // 添加日志检查
            }
            
            return isReturned;  // 返回是否有结果
        }
    }
}
