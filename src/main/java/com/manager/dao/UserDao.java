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
        }
    }
}
