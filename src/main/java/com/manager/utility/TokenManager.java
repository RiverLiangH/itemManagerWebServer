package com.manager.utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class TokenManager {
    private static final String TEMP_USERS_FILE = "temp_users.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <InputStream> File getTempUsersFile() {
        // 获取资源文件的输入流
        InputStream resourceStream = (InputStream) TokenManager.class.getClassLoader().getResourceAsStream(TEMP_USERS_FILE);
        if (resourceStream == null) {
            throw new IllegalArgumentException("File not found: " + TEMP_USERS_FILE);
        }
        // 将文件路径转为 File 对象
        return new File(TokenManager.class.getClassLoader().getResource(TEMP_USERS_FILE).getFile());
    }

    // 保存用户数据到文件
    public static void saveUserData(String email, String name, String phone, String password, boolean admin, String token, long expiry) throws IOException {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("admin", admin);
        userData.put("token", token);
        userData.put("expiry", expiry);

        Map<String, Map<String, Object>> allUsers = loadAllUserData();
        allUsers.put(email, userData);
        objectMapper.writeValue(new File(TEMP_USERS_FILE), allUsers);
    }

    // 从文件中加载所有用户数据
    private static Map<String, Map<String, Object>> loadAllUserData() throws IOException {
        File file = new File(TEMP_USERS_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }
        return objectMapper.readValue(file, Map.class);
    }

    // 验证 token 是否有效
    public static boolean isTokenValid(String email, String token) throws IOException {
        Map<String, Map<String, Object>> allUsers = loadAllUserData();
        Map<String, Object> userData = allUsers.get(email);

        if (userData != null && userData.get("token").equals(token)) {
            long expiry = (long) userData.get("expiry");
            return System.currentTimeMillis() <= expiry;
        }
        return false;
    }

    // 获取用户数据
    public static Map<String, Object> getUserData(String email) throws IOException {
        Map<String, Map<String, Object>> allUsers = loadAllUserData();
        return allUsers.get(email);
    }

    // 删除用户数据
    public static void removeUser(String email) throws IOException {
        Map<String, Map<String, Object>> allUsers = loadAllUserData();
        allUsers.remove(email);
        objectMapper.writeValue(new File(TEMP_USERS_FILE), allUsers);
    }
}