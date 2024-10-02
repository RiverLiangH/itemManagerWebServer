package com.manager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.manager.model.User;
import com.manager.dao.DatabaseConnection;

public class UserDao {

    // Method to insert a new user into the database
    public void insertUser(User user) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO user (name, phone, email, password, admin) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setBoolean(5, user.isAdmin());

            stmt.executeUpdate();  // This executes the SQL statement
        }catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to insert user", e);
        }
    }

    // Method to update an existing user in the database
    public void updateUser(User user) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "UPDATE user SET name = ?, phone = ?, email = ?, password = ?, admin = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setBoolean(5, user.isAdmin());
            stmt.setInt(6, user.getId());

            stmt.executeUpdate();  // This updates the existing record
        }catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to update user", e);
        }
    }

    // Method to retrieve a user from the database by ID
    public User getUserById(int userId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM user WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Create a User object based on the result set
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setAdmin(rs.getBoolean("admin"));

                return user;
            } else {
                return null;  // No user found
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to retrieve user by ID", e);
        }
    }

    // Method to retrieve a user from the database by name
    public User getUserByName(String userName) throws SQLException {
        String query = "SELECT * FROM user WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userName);  // 将用户名作为查询条件传递
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // 从查询结果集中创建 User 对象
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setAdmin(rs.getInt("admin") == 1);

                return user;  // 返回查询到的用户
            } else {
                return null;  // 未找到匹配的用户
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to retrieve user by name", e);
        }
    }
}
