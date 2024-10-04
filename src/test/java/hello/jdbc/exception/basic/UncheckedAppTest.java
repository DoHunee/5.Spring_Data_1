package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedAppTest {

    // 테스트: 런타임 예외가 던져지는지 확인
    @Test
    void unchecked() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class); // Exception이 발생하는지 확인
    }

    // 예외 발생 시 로그 출력 테스트
    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            // e.printStackTrace();
            log.info("ex", e);
        }
    }

    public void call() {
        try {
            runSQL();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e); // 기존 예외(e) 포함
            // throw new RuntimeSQLException(); //기존 예외(e) 제외
        }
    }

    public void runSQL() throws SQLException {
        throw new SQLException("ex");
    }

    // Controller 클래스: Service 호출
    static class Controller {
        Service service = new Service();

        // 런타임 예외 던짐
        public void request() {
            service.logic();
        }
    }

    // Service 클래스: Repository와 NetworkClient 호출
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        // 런타임 예외를 처리하지 않고 상위로 던짐
        public void logic() {
            repository.call(); // DB 호출
            networkClient.call(); // 네트워크 호출
        }
    }

    // NetworkClient 클래스: 네트워크 호출 중 런타임 예외 던짐
    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패"); // 네트워크 연결 실패 시 예외 발생
        }
    }

    // Repository 클래스: DB 작업 중 런타임 예외 던짐
    static class Repository {
        public void call() {
            try {
                runSQL(); // SQL 작업 호출
            } catch (SQLException e) {
                // 체크 예외를 런타임 예외로 전환해서 던짐
                throw new RuntimeSQLException(e);
            }
        }

        // SQL 작업 중 예외 발생
        private void runSQL() throws SQLException {
            throw new SQLException("ex"); // SQL 작업 실패 시 예외 발생
        }
    }

    // 커스텀 런타임 예외: 네트워크 연결 실패
    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    // 커스텀 런타임 예외: SQL 처리 실패
    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException() {
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause); // 기존 예외를 포함한 런타임 예외로 변환
        }
    }
}