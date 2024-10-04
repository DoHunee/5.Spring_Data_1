package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.net.ConnectException;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedAppTest {

    // JUnit 테스트 메서드
    @Test
    void checked() {
        Controller controller = new Controller();

        // controller.request() 호출 시 SQLException 또는 ConnectException이 발생할 수 있으며,
        // 이 예외가 제대로 던져지는지 assertThatThrownBy로 검증
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class); // Exception 타입의 예외가 발생하는지 확인
    }

    // Controller 클래스: Service 호출, 예외 처리 없이 체크 예외를 밖으로 던짐
    static class Controller {
        Service service = new Service();

        // request 메서드에서 SQLException과 ConnectException을 던질 수 있음
        public void request() throws SQLException, ConnectException {
            service.logic(); // Service의 로직을 호출
        }
    }

    // Service 클래스: Repository와 NetworkClient를 호출하여 체크 예외를 발생시킬 수 있음
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        // Service 로직을 처리하며, SQLException과 ConnectException을 던질 수 있음
        public void logic() throws SQLException, ConnectException {
            repository.call(); // DB 작업 중 SQLException 발생 가능
            networkClient.call(); // 네트워크 작업 중 ConnectException 발생 가능
        }
    }

    // NetworkClient 클래스: 네트워크 연결 처리
    static class NetworkClient {

        // 네트워크 연결 실패 시 ConnectException을 던짐
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패"); // 예외 발생: 네트워크 연결 실패 시
        }
    }

    // Repository 클래스: 데이터베이스 작업 처리
    static class Repository {

        // DB 작업 중 SQLException을 던질 수 있음
        public void call() throws SQLException {
            throw new SQLException("ex"); // 예외 발생: SQL 작업 중 문제 발생 시
        }
    }
}