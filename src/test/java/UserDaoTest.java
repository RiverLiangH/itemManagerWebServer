import com.manager.dao.UserDao;
import com.manager.model.User;
import com.manager.dao.DatabaseConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class UserDaoTest {

    private UserDao userDao;
    private Connection conn;

    // 在每个测试之前运行，用于初始化测试对象和清理环境
    @Before
    public void setUp() throws SQLException {
        userDao = new UserDao();
        conn = DatabaseConnection.getConnection();
        // 清理表中所有数据以确保测试独立
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM user")) {
            stmt.executeUpdate();
        }
    }

    // 插入用户的测试
    @Test
    public void testInsertUser() throws SQLException {
        User user = new User(0, "Alice", "123456789", "alice@example.com", "alicepassword", true); // ID 为 0，让数据库自动生成
        userDao.insertUser(user);
        System.out.println("Inserting user: " + user.getName());

        // 验证用户插入是否成功
        User insertedUser = userDao.getUserByName("Alice");
        assertNotNull(insertedUser);
        assertEquals("Alice", insertedUser.getName());
        assertEquals("123456789", insertedUser.getPhone());
    }

    // 更新用户的测试
    @Test
    public void testUpdateUser() throws SQLException {
        // 插入一个用户
        User user = new User(0, "Bob", "987654321", "bob@example.com", "oldpassword", true);
        userDao.insertUser(user);

        // 从数据库获取插入用户的 ID
        User insertedUser = userDao.getUserByName("Bob");
        assertNotNull(insertedUser);
        int userId = insertedUser.getId();

        // 更新用户信息
        insertedUser.setName("Bob Updated");
        insertedUser.setPassword("newpassword");
        userDao.updateUser(insertedUser);

        // 验证用户更新是否成功
        User updatedUser = userDao.getUserById(userId);
        assertNotNull(updatedUser);
        assertEquals("Bob Updated", updatedUser.getName());
        assertEquals("newpassword", updatedUser.getPassword());
    }

    // 根据ID获取用户的测试
    @Test
    public void testGetUserById() throws SQLException {
        // 插入一个用户
        User user = new User(0, "Charlie", "123456789", "charlie@example.com", "password", false);
        userDao.insertUser(user);

        // 从数据库获取插入用户的 ID
        User insertedUser = userDao.getUserByName("Charlie");
        assertNotNull(insertedUser);
        int userId = insertedUser.getId();

        // 根据 ID 验证查询用户是否正确
        User retrievedUser = userDao.getUserById(userId);
        assertNotNull(retrievedUser);
        assertEquals("Charlie", retrievedUser.getName());
        assertEquals("charlie@example.com", retrievedUser.getEmail());
    }

    // 在每个测试后运行，清理测试数据
    @After
    public void tearDown() throws SQLException {
        // 清理表中的所有数据
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM user")) {
            stmt.executeUpdate();
        }
        // 关闭数据库连接
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
