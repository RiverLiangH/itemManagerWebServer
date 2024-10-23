package com.manager.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TokenManager {

    // 检查 token 是否存在并且未过期
    public static boolean validateToken(String email, String token) throws IOException {
        String filePath = "tokens.txt";
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String storedEmail = parts[0];
                String storedToken = parts[1];
                LocalDateTime tokenCreationTime = LocalDateTime.parse(parts[2]);

                if (storedEmail.equals(email) && storedToken.equals(token)) {
                    // 设定 token 过期时间，比如 24 小时
                    LocalDateTime now = LocalDateTime.now();
                    if (ChronoUnit.HOURS.between(tokenCreationTime, now) < 24) {
                        return true; // token 有效
                    } else {
                        System.out.println("Token expired.");
                        return false; // token 过期
                    }
                }
            }
        }
        System.out.println("Invalid token or email.");
        return false; // token 不存在
    }

    public static void main(String[] args) throws IOException {
        String email = "user@example.com";
        String token = "这里放用户的token"; // 用户验证时提供的 token

        boolean isValid = validateToken(email, token);
        if (isValid) {
            System.out.println("Token is valid.");
        } else {
            System.out.println("Token is invalid or expired.");
        }
    }
}

