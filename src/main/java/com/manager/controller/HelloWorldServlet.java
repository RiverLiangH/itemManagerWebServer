package com.manager.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/hello")
public class HelloWorldServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Set return type JSON
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            // Construct JSON response
            String jsonResponse = "{\"message\": \"Hello, World!\"}";

            // Output JSON response
            resp.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace to logs
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred.");
        }
    }
}
