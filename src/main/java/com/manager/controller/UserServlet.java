package com.manager.controller;

import com.manager.dao.UserDao;
import com.manager.model.User;
import java.sql.SQLException;  // 导入 SQLException

import com.manager.utility.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private UserDao userDao = new UserDao();

    // 处理 GET 请求：根据ID或名称查询用户
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // 获取所有用户信息或根据 ID/用户名查询用户
            if (pathInfo == null || pathInfo.equals("/")) {
                // 获取所有用户
                out.println(userDao.getAllUsers());
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 3 && "idByUsername".equals(pathParts[1])) {
                    // 根据用户名查询用户
                    String userName = pathParts[2];
                    User user = userDao.getUserByName(userName);
                    if (user != null) {
                        // 将用户信息转换为 JSON 格式并输出
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("user_id", user.getId());
                        jsonResponse.put("user_name", user.getName());
                        jsonResponse.put("user_phone", user.getPhone());
                        jsonResponse.put("user_email", user.getEmail());
                        jsonResponse.put("admin", user.isAdmin());

                        out.println(jsonResponse.toString());
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println("{\"error\": \"User not found\"}");
                    }
                } else if (pathParts.length == 2) {
                    // 根据ID查询用户
                    int userId = Integer.parseInt(pathParts[1]);
                    User user = userDao.getUserById(userId);
                    if (user != null) {
                        // 将用户信息转换为 JSON 格式并输出
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("user_id", user.getId());
                        jsonResponse.put("user_name", user.getName());
                        jsonResponse.put("user_phone", user.getPhone());
                        jsonResponse.put("user_email", user.getEmail());
                        jsonResponse.put("admin", user.isAdmin());

                        out.println(jsonResponse.toString());
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println("{\"error\": \"User not found\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Invalid request\"}");
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching user data", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error fetching user data\"}");
        }
    }


    // 处理 POST 请求：创建新用户
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Received POST request");
        response.setContentType("application/json");
        String path = request.getPathInfo();

        PrintWriter out = response.getWriter();


        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            if ("/create".equals(path)) {
                // 使用 JSON 库解析请求体中的 JSON 数据
                String requestBody = sb.toString();
                JSONObject jsonObject = new JSONObject(requestBody);

                String name = jsonObject.optString("user_name");
                String phone = jsonObject.optString("user_phone");
                String email = jsonObject.optString("user_email");
                String password = jsonObject.optString("user_password");
                boolean admin = jsonObject.optBoolean("admin");

                if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \"Missing required fields\"}");
                    return;
                }

                // 创建用户对象并插入数据库（ID 由数据库自动生成）
                User user = new User(0, name, phone, email, password, admin);
                userDao.insertUser(user);
                ;  // 获取生成的自增 ID
                logger.info("User created successfully: {}", name);

                // 返回创建成功的用户信息以及生成的 user_id
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("success", true);
                jsonResponse.put("message", "User created successfully");
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.println(jsonResponse.toString());
            } else if ("/login".equals(path)) {
                String requestBody = sb.toString();
                JSONObject jsonObject = new JSONObject(requestBody);

                String email = jsonObject.optString("email");
                String password = jsonObject.optString("password");

                User user = userDao.findUserByEmailAndPassword(email, password);
                if (user != null) {
                    // 用户验证成功，生成 JWT Token
                    String token = JwtUtil.generateToken(user.getId());

                    // 返回 Token 给客户端
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"token\": \"" + token + "\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"message\": \"Invalid email or password\"}");
                }


            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }



        } catch (SQLException e) {
            logger.error("Error creating user", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Failed to create user\"}");
        }
    }

    // 处理 PUT 请求：更新用户
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // 获取 Authorization 头中的 token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            // 如果没有 token 或者 token 格式不正确，返回 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println("{\"error\": \"Missing or invalid Authorization header\"}");
            return;
        }

        // 验证 token 的有效性并获取用户ID
        token = token.substring(7); // 去掉 "Bearer " 前缀
        String userIdStr = JwtUtil.validateToken(token);
        if (userIdStr == null) {
            // 如果 token 无效，返回 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println("{\"error\": \"Unauthorized: Invalid token\"}");
            return;
        }

        // 将 userIdStr 转换为整型
        int userId = Integer.parseInt(userIdStr);

        // 读取请求体中的 JSON 数据
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            // 将请求体转换为 JSON 对象
            String requestBody = sb.toString();
            JSONObject jsonObject = new JSONObject(requestBody);

            String name = jsonObject.optString("user_name");
            String phone = jsonObject.optString("user_phone");
            String email = jsonObject.optString("user_email");
            String password = jsonObject.optString("user_password");
            boolean admin = jsonObject.optBoolean("admin");

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing required fields\"}");
                return;
            }

            // 更新用户信息，使用从 token 中解码出来的 userId
            User user = new User(userId, name, phone, email, password, admin);
            try {
                userDao.updateUser(user);
                logger.info("User updated successfully: {}", name);

                response.setStatus(HttpServletResponse.SC_OK);
                out.println("{\"success\": true, \"message\": \"User updated successfully\"}");
            } catch (SQLException e) {
                logger.error("Error updating user", e);
                if (e.getMessage().contains("does not exist")) {
                    // 用户不存在的错误
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.println("{\"error\": \"User not found with ID: " + userId + "\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("{\"error\": \"Failed to update user\"}");
                }
            }

        } catch (Exception e) {
            logger.error("Error processing update request", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Invalid request format\"}");
        }
    }




    // 处理 DELETE 请求：删除用户
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                int userId = Integer.parseInt(pathInfo.split("/")[1]);
                userDao.deleteUser(userId);
                logger.info("User deleted successfully: user_id={}", userId);
                out.println("User deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("Invalid request");
            }
        } catch (SQLException e) {
            logger.error("Error deleting user", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error deleting user");
        }
    }
}


