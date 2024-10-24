package com.manager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.manager.dao.ItemDao;
import com.manager.model.Item;
import com.manager.dao.UserDao;
import com.manager.utility.JwtUtil;

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

    // 统一的管理员权限验证方法
    private boolean verifyAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        // 获取 Authorization 头中的 token
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            // 如果没有 token 或者 token 格式不正确，返回 401 Unauthorized
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
            return false;
        }

        // 验证 token 的有效性
        token = token.substring(7); // 去掉 "Bearer " 前缀
        String userIdStr = JwtUtil.validateToken(token);
        if (userIdStr == null) {
            // 如果 token 无效，返回 401 Unauthorized
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Unauthorized: Invalid token\"}");
            return false;
        }

        // 获取用户角色，确保用户是管理员
        UserDao userDao = new UserDao();
        String userRole = userDao.getRoleByUserId(Integer.parseInt(userIdStr));
        if (userRole == null || !"ADMIN".equals(userRole)) {
            // 如果用户不是管理员，返回 403 Forbidden
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("{\"error\": \"Access denied: You must be an admin to perform this operation\"}");
            return false;
        }

        // 验证通过
        return true;
    }

    // 处理 POST 请求：增加物品
    // 处理 POST 请求：删除物品
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*"); // 允许所有域名访问
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // 允许的方法
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type"); // 允许的请求头
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("Received POST request");

        try {
            // 设置返回类型为 JSON
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            // 验证管理员权限
            if (!verifyAdmin(req, resp)) {
                return; // 如果不是管理员，直接返回，不执行后续逻辑
            }

            // 获取请求路径
            String path = req.getPathInfo();
            String jsonResponse = "{\"message\": \"Hello, World!\"}";

            if ("/add".equals(path)) {
                // 读取请求体中的 JSON 数据
                BufferedReader reader = req.getReader();
                StringBuilder jsonInput = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonInput.append(line);
                }

                // 解析 JSON 数据
                String requestData = jsonInput.toString();
                JSONObject jsonObject = new JSONObject(requestData);
                String name = jsonObject.getString("name");
                String type = jsonObject.getString("type");
                // int location = jsonObject.getInt("location");

                // 创建 Item 对象
                Item item = new Item();
                item.setName(name);
                item.setType(type);
                item.setLocation(1);

                // 插入 Item 到数据库
                itemDao.insertItem(item);

                // 返回成功消息
                jsonResponse = "{\"message\": \"Item added successfully\"}";
                resp.getWriter().write(jsonResponse);
            } else if ("/delete".equals(path)) {
                // resp.getWriter().write("Fetching product...");
                BufferedReader reader = req.getReader();
                StringBuilder jsonInput = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonInput.append(line);
                }

                System.out.println("Received JSON: " + jsonInput.toString());

                JSONObject jsonObject = new JSONObject(jsonInput.toString());
                String itemName = jsonObject.getString("name"); // 获取要删除的 item_name

                itemDao.deleteRandomItemByItemName(itemName);

                // 设置成功响应消息
                jsonResponse = "{\"message\": \"Item deleted successfully\"}";
                resp.getWriter().write(jsonResponse);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

            // Output JSON response
            // resp.getWriter().write(jsonResponse);
        } catch (SQLException e) {
            e.printStackTrace(); // 打印堆栈信息到日志
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // 打印堆栈信息到日志
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*"); // 允许所有域名访问
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // 允许的方法
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type"); // 允许的请求头
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("Received GET request");

        try {
            // 设置返回类型为 JSON
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

//            // 验证管理员权限
//            if (!verifyAdmin(req, resp)) {
//                return; // 如果不是管理员，直接返回，不执行后续逻辑
//            }

            // 获取请求路径
            String path = req.getPathInfo();

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
            } else if ("/records".equals(path)) {
                System.out.println("Fetching items from the database...");
                JSONArray itemsArray = itemDao.getAllItemRecords();

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