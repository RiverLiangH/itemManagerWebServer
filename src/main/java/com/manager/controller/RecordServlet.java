package com.manager.controller;

import com.manager.dao.RecordDao;
import com.manager.model.UserBorrowRecord;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

public class RecordServlet extends HttpServlet {

    private RecordDao recordDao = new RecordDao();

    // 处理 POST 请求：借用物品
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // 读取请求体中的 JSON 数据
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            // 使用 JSON 库解析请求体中的 JSON 数据
            String requestBody = sb.toString();
            JSONObject jsonObject = new JSONObject(requestBody);

            int userId = jsonObject.getInt("user_id");
            int itemId = jsonObject.getInt("item_id");

            if (userId == 0 || itemId == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing required fields\"}");
                return;
            }

            // 创建借物记录
            UserBorrowRecord record = new UserBorrowRecord();
            record.setUserId(userId);
            record.setItemId(itemId);
            record.setBorrowTime(Time.valueOf(LocalTime.now()));

            recordDao.insertRecord(record);

            response.setStatus(HttpServletResponse.SC_CREATED);
            out.println("{\"success\": true, \"message\": \"Item borrowed successfully\"}");

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Failed to borrow item\"}");
        }
    }

    // 处理 PUT 请求：归还物品
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // 读取请求体中的 JSON 数据
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        try {
            // 使用 JSON 库解析请求体中的 JSON 数据
            String requestBody = sb.toString();
            JSONObject jsonObject = new JSONObject(requestBody);

            int recordId = jsonObject.getInt("record_id");

            if (recordId == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Missing required fields\"}");
                return;
            }

            // 更新归还时间
            recordDao.updateReturnTime(recordId, Time.valueOf(LocalTime.now()));

            response.setStatus(HttpServletResponse.SC_OK);
            out.println("{\"success\": true, \"message\": \"Item returned successfully\"}");

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Failed to return item\"}");
        }
    }

    // 处理 GET 请求：查询用户的所有借物记录
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"error\": \"Invalid request\"}");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int userId = Integer.parseInt(pathParts[1]);
                // 查询用户的借物记录
                List<UserBorrowRecord> records = recordDao.getRecordsByUserId(userId);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("records", records);
                out.println(jsonResponse.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Invalid request\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Error fetching records\"}");
        }
    }
}
