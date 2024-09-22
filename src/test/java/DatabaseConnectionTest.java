import com.manager.dao.DatabaseConnection;
import org.junit.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;

public class DatabaseConnectionTest {

    @Test
    public void testConnection() throws SQLException {
        // 获取数据库连接
        Connection connection = DatabaseConnection.getConnection();

        // 检查连接是否为 null
        assertNotNull("Database Connect Fail", connection);

        // 关闭连接（可选）
        if (connection != null) {
            try {
                assertNotNull("Database Connect Succ", connection);
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

