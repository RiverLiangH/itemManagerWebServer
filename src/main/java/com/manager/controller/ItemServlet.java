package com.manager.controller;

import com.manager.utility.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.manager.dao.ItemDao;
import com.manager.model.Item;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONObject; // For parsing JSON input
import org.json.JSONArray;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/items/*")
public class ItemServlet extends HttpServlet {
    private ItemDao itemDao;

    @Override
    public void init() throws ServletException {
        super.init();
        itemDao = new ItemDao(); // Make sure to init itemDao HERE
    }

    // Manager: add item/delete item
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Received POST request");
        String token = req.getHeader("Authorization");
        String jsonResponse = "{\"message\": \"Hello, World!\"}";

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
            String userIdStr = JwtUtil.validateToken(token);

            if (userIdStr != null) {
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                try {
                    String path = req.getPathInfo();
                    System.out.println("Request URI: " + path);
                    BufferedReader reader = req.getReader();
                    StringBuilder jsonInput = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        jsonInput.append(line);
                    }

                    if ("/add".equals(path)) {
                        System.out.println("Received POST request: add an item");
                        String requestData = jsonInput.toString();
                        JSONObject jsonObject = new JSONObject(requestData); // Use any JSON parsing library here
                        String name = jsonObject.getString("name");
                        String type = jsonObject.getString("type");
                        int location = jsonObject.getInt("location");

                        // Create an Item object
                        Item item = new Item();
                        item.setName(name);
                        item.setType(type);
                        item.setLocation(location);

                        // Insert the item into the database
                        itemDao.insertItem(item);

                        // Set success response message
                        jsonResponse = "{\"message\": \"Item added successfully\"}";
                        resp.getWriter().write(jsonResponse);
                    } else if ("/delete".equals(path)) {
                        System.out.println("Received JSON: " + jsonInput.toString());

                        JSONObject jsonObject = new JSONObject(jsonInput.toString());
                        int itemId = jsonObject.getInt("id"); // 获取要删除的 itemId

                        itemDao.deleteItem(itemId);

                        // 设置成功响应消息
                        jsonResponse = "{\"message\": \"Item deleted successfully\"}";
                        resp.getWriter().write(jsonResponse);
                    } else {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } catch (SQLException e) {
                    e.printStackTrace(); // Print stack trace to logs
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace(); // Print stack trace to logs
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred.");
                }
            } else {
                // 验证失败，返回 401
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Unauthorized");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Received GET request");
        try {
            String path = req.getPathInfo();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            if ("/info".equals(path)) {
                System.out.println("Fetching items from the database...");
                JSONArray itemsArray = itemDao.getAllItems(); // 调用查询所有物品的方法

                // 检查是否查询到了数据
                if (itemsArray.length() > 0) {
                    System.out.println("Items found: " + itemsArray.length());
                    resp.getWriter().write(itemsArray.toString()); // 返回 JSON 数据
                } else {
                    System.out.println("No items found.");
                    resp.getWriter().write("[]"); // 如果没有数据，返回空数组
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }
}
