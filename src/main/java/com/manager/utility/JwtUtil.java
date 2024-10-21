package com.manager.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "GentleReminderOnTheUpcomingAssessmentF2FInClassTermPaperDuration2HoursThisIsAClosedBookTestAsItIsAPrintedTestPaperPleaseRememberToBringYourOwnStationery"; // 用于签名 Token 的密钥
    private static final long EXPIRATION_TIME = 10800000; // Token 有效期，3 h

    // 生成 JWT Token
    public static String generateToken(Integer userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 将 userId 作为 Token 的 subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 验证 JWT Token，返回解析后的 userId
    public static String validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // 获取 Token 中的 subject，即 userId
        } catch (Exception e) {
            return null; // 如果 Token 无效或过期，返回 null
        }
    }
}
