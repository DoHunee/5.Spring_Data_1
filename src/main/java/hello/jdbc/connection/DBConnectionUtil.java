package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {
        try {
            // DriverManager.getConnection() 메서드를 사용해서 데이터베이스 커넥션을 획득한다.
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } 
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}