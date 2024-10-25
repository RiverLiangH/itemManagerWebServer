package com.manager.controller;

import com.manager.dao.ItemDao;
import com.manager.dao.RecordDao;
import com.manager.model.UserBorrowRecord;
import com.manager.model.Item;
import com.manager.utility.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;



import java.util.logging.Logger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@WebServlet("/api/records/*")
public class RecordServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RecordServlet.class.getName());
    private RecordDao recordDao;
    private ItemDao itemDao;

    public void init() throws ServletException {
        super.init();
        recordDao = new RecordDao();
        itemDao = new ItemDao();  // 初始化 ItemDao
    }

    // 处理 Options 请求
    // 跨域问题
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*"); // 允许所有域名访问
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // 允许的方法
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization"); // 允许的请求头
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String token = request.getHeader("Authorization");
        String jsonResponse = "{\"message\": \"Hello, World!\"}";

    }

    // 处理 POST 请求：借用物品
    // 处理 POST 请求：归还物品
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*"); // 允许所有域名访问
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // 允许的方法
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization"); // 允许的请求头
        response.setHeader("Access-Control-Allow-Credentials", "true");
        logger.info("Received POST request: record doPost");
        String token = request.getHeader("Authorization");
        String jsonResponse = "{\"message\": \"Hello, World!\"}";

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
            String userIdStr = JwtUtil.validateToken(token);

            if (userIdStr != null) {
                // 验证通过，返回受保护的内容
                response.setStatus(HttpServletResponse.SC_OK);
                // response.getWriter().write("Welcome, " + username);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();

                // 读取请求体中的 JSON 数据
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                logger.info("Request Body: " + sb.toString());

                try {
                    String path = request.getPathInfo();
                    /*
                    *   API: Borrow item
                    *   Author: Jkc
                    *
                    */
                    System.out.println("Path Info: " + path); // 打印路径信息

                    logger.info("Path Info: " + path);

                    if ("/borrow".equals(path)) {
                        logger.info("Processing borrow request");

                        // 从请求体中获取物品名称
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        String itemName = jsonObject.getString("item_name");  // 前端提供物品名称
                        // 获取用户ID
                        int userId = Integer.parseInt(userIdStr);
                        // 查询用户已经借用了多少个同名物品
                        int borrowedItemCount = recordDao.countBorrowedItemsByName(userId, itemName);
                        if (borrowedItemCount >= 3) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            out.println("{\"error\": \"You have already borrowed more than 3 items with the same name\"}");
                            return;
                        }
                        // 根据物品名称查找可借用的物品
                        Item itemToBorrow = itemDao.getAvailableItemsByName(itemName);

                        if (itemToBorrow == null) {
                            // 没有找到可借用的物品
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            jsonResponse = "{\"error\": \"No available items with the name " + itemName + "\"}";
                            out.println(jsonResponse);
                            return;
                        }



                        // 检查用户是否已经借用了该物品且未归还
                        if (recordDao.isItemBorrowed(userId, itemToBorrow.getId())) {
                            response.setStatus(HttpServletResponse.SC_CONFLICT);
                            jsonResponse = "{\"error\": \"You have already borrowed this item and not yet returned it.\"}";
                            out.println(jsonResponse);
                            return;
                        }

                        // 创建借用记录
                        UserBorrowRecord record = new UserBorrowRecord();
                        record.setUserId(userId);
                        record.setItemId(itemToBorrow.getId());
                        // 使用 Timestamp 获取当前日期和时间
                        record.setBorrowTime(new Timestamp(System.currentTimeMillis()));


                        // 插入借用记录
                        recordDao.insertRecord(record);

                        // 更新物品状态为已借出 (current_condition = 0)
                        itemDao.updateItemCondition(itemToBorrow.getId(), 0);

                        // 返回借用成功的 item_id 给前端
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        jsonResponse = "{\"success\": true, \"message\": \"Item borrowed successfully\", \"item_id\": " + itemToBorrow.getId() + "}";
                        out.println(jsonResponse);

                    
                        // out.println("{\"success\": true, \"message\": \"Item borrowed successfully\"}");
                    } else if ("/return".equals(path)) {
                        /*
                         *   API: Return item
                         *   Author: Jkc
                         *
                         */
                        String requestBody = sb.toString();

                        logger.info("Processing return request");
                        JSONObject jsonObject = new JSONObject(sb.toString());
                        int recordId = jsonObject.getInt("record_id");
                        System.out.println("Entered return logic");

                        if (recordId == 0) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            jsonResponse = "{\"error\": \"Missing required fields\"}";
                            response.getWriter().write(jsonResponse);
                            // out.println("{\"error\": \"Missing required fields\"}");
                            return;
                        }

                        // 检查是否已经归还
                        if (recordDao.isItemReturned(recordId)) {
                            response.setStatus(HttpServletResponse.SC_CONFLICT);
                            jsonResponse = "{\"error\": \"Item has already been returned\"}";
                            out.println(jsonResponse);
                            return;
                        }

                        // 更新归还时间
                        recordDao.updateReturnTime(recordId, Timestamp.valueOf(LocalDateTime.now()));

                        // 更新 item 表的 current_condition 为 1（归还状态）
                        UserBorrowRecord record = recordDao.getRecordById(recordId);
                        if (record != null) {
                            itemDao.updateItemCondition(record.getItemId(), 1);
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.println("{\"success\": true, \"message\": \"Item returned successfully\"}");
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            jsonResponse = "{\"error\": \"Record not found\"}";
                            out.println(jsonResponse);
                            return;
                        }

                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }

                } catch (SQLException e) {
                    logger.severe("SQL Error: " + e.getMessage());
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\": \"Database error: " + e.getMessage() + "\"}");
                } catch (Exception e) {
                    logger.severe("Unexpected Error: " + e.getMessage());
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\": \"Unexpected error: " + e.getMessage() + "\"}");
                }
                return;
            } else {
                // 验证失败，返回 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized");
            }
        }

    }




    // 处理 GET 请求：查询用户的所有借物记录
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
    
        String token = request.getHeader("Authorization");
    
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println("{\"error\": \"Missing or invalid Authorization header\"}");
            return;
        }
    
        try {
            token = token.substring(7);  // 去掉 "Bearer " 前缀
            String userIdStr = JwtUtil.validateToken(token);  // 解码 token 获取 userId
    
            if (userIdStr == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"error\": \"Invalid token\"}");
                return;
            }
    
            int userId = Integer.parseInt(userIdStr);
    
            // 查询用户的详细借物记录
            List<Map<String, Object>> records = recordDao.getDetailedRecordsByUserId(userId);
    
            JSONArray recordsArray = new JSONArray();
            for (Map<String, Object> record : records) {
                JSONObject recordJson = new JSONObject();
                recordJson.put("record_id", record.get("record_id"));
                recordJson.put("user_id", record.get("user_id"));
                recordJson.put("item_id", record.get("item_id"));
                recordJson.put("item_name", record.get("item_name"));
                recordJson.put("item_type", record.get("item_type"));
                recordJson.put("borrow_time", record.get("borrow_time"));
                // 如果有 return_time，才返回
                if (record.get("return_time") != null) {
                    recordJson.put("return_time", record.get("return_time"));
                }
    
                recordsArray.put(recordJson);
            }
    
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("records", recordsArray);
    
            out.println(jsonResponse.toString());
    
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error fetching records\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Unexpected error\"}");
        }
    }
    

}
