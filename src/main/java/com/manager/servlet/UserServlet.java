package com.manager.servlet;

import com.manager.dao.UserDao;
import com.manager.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class UserServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    // 处理 GET 请求：根据ID或名称查询用户
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 获取所有用户
                out.println(userDao.getAllUsers());
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    // 根据ID查询用户
                    int userId = Integer.parseInt(pathParts[1]);
                    User user = userDao.getUserById(userId);
                    if (user != null) {
                        out.println(user);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println("User not found");
                    }
                } else if (pathParts.length == 3 && "name".equals(pathParts[1])) {
                    // 根据名称查询用户
                    String userName = pathParts[2];
                    User user = userDao.getUserByName(userName);
                    if (user != null) {
                        out.println(user);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println("User not found");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("Invalid request");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error fetching user data");
        }
    }

    // 处理 POST 请求：创建新用户
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            boolean admin = Boolean.parseBoolean(request.getParameter("admin"));

            User user = new User(0, name, phone, email, password, admin);
            userDao.insertUser(user);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.println("User created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error creating user");
        }
    }

    // 处理 PUT 请求：更新用户
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.split("/").length == 2) {
                int userId = Integer.parseInt(pathInfo.split("/")[1]);
                String name = request.getParameter("name");
                String phone = request.getParameter("phone");
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                boolean admin = Boolean.parseBoolean(request.getParameter("admin"));

                User user = new User(userId, name, phone, email, password, admin);
                userDao.updateUser(user);
                out.println("User updated successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("Invalid request");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error updating user");
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
                out.println("User deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("Invalid request");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error deleting user");
        }
    }
}
