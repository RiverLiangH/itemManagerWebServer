
// package com.manager.filter;
// import java.io.IOException;
// import jakarta.servlet.Filter;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.FilterConfig;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.ServletRequest;
// import jakarta.servlet.ServletResponse;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// public class CORSFilter implements Filter {

//     @Override
//     public void init(FilterConfig filterConfig) throws ServletException {}

//     @Override
//     public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//         HttpServletResponse res = (HttpServletResponse) response;
//         HttpServletRequest req = (HttpServletRequest) request;

//         // 设置 CORS 响应头
//         res.setHeader("Access-Control-Allow-Origin", "*");
//         res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
//         res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
//         res.setHeader("Access-Control-Allow-Credentials", "false");

//         // 如果是 OPTIONS 请求，则提前返回
//         if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
//             res.setStatus(HttpServletResponse.SC_OK);
//             return;
//         }

//         // 否则继续处理请求
//         chain.doFilter(request, response);
//     }

//     @Override
//     public void destroy() {}
// }
