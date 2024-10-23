package com.manager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.manager.model.User;
import com.manager.dao.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

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
    
            stmt.executeUpdate();
            logger.info("Inserting user into database with name: {}, phone: {}, email: {}, admin: {}",
                    user.getName(), user.getPhone(), user.getEmail(), user.isAdmin());
            logger.info("User inserted successfully");
        } catch (SQLException e) {
            // 加入日志打印
            System.out.println("SQL Error while inserting user: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("With parameters: " + user.getName() + ", " + user.getPhone() + ", " + user.getEmail() + ", " + user.getPassword() + ", " + user.isAdmin());
            logger.error("Error inserting user into database", e);
            throw new SQLException("Failed to insert user", e);
        }
    }

    public boolean existUserEmail(String email) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String checkQuery = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating user", e);
            throw new SQLException("Failed to update user", e);
        }

        return false;
    }

    // Method to update an existing user in the database
    public void updateUser(User user) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        // 首先检查用户是否存在
        String checkQuery = "SELECT COUNT(*) FROM user WHERE id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, user.getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) {
                    // 用户不存在
                    throw new SQLException("User with id " + user.getId() + " does not exist");
                }
            }
        }
        // 根据是否需要更新密码，生成不同的 SQL 语句
        String query;
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            // 如果密码为空，则不更新密码
            query = "UPDATE user SET name = ?, phone = ?, email = ?, admin = ? WHERE id = ?";
        } else {
            // 如果有密码，则更新密码
            query = "UPDATE user SET name = ?, phone = ?, email = ?, password = ?, admin = ? WHERE id = ?";
        }
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getEmail());
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                // 不更新密码的情况下，设置 admin 和 id
                stmt.setBoolean(4, user.isAdmin());
                stmt.setInt(5, user.getId());
            } else {
                // 更新密码的情况下，设置 password、admin 和 id
                stmt.setString(4, user.getPassword());
                stmt.setBoolean(5, user.isAdmin());
                stmt.setInt(6, user.getId());
            }
    
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating user", e);
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

    public User findUserByEmailAndPassword(String email, String password) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

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

                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to retrieve user by email and password", e);
        }
    }
    
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM user";
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setAdmin(rs.getBoolean("admin"));
    
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching all users", e);
        }
    
        return users;
    }


    public void deleteUser(int userId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "DELETE FROM user WHERE id = ?";
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error deleting user with ID: " + userId, e);
        }
    }
    
}
