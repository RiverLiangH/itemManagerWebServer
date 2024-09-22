package com.manager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConnection {

    private static String url;
    private static String username;
    private static String password;
    private static String driverClass;

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("无法找到 db.properties 文件");
            }
            Properties properties = new Properties();
            properties.load(input);

            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            driverClass = properties.getProperty("db.driverClass");

            Class.forName(driverClass); // 注册JDBC驱动程序
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (url == null) {
            throw new SQLException("Database URL is not defined");
        }
        System.out.println("Database URL: " + url);  // 调试输出
        return DriverManager.getConnection(url, username, password);
    }
}
